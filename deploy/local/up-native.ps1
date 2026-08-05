# JinShiFang native middleware: Redis / MongoDB / MinIO
# MySQL uses Windows service MySQL80 (not started here).
# Usage: powershell -File deploy\local\up-native.ps1
# Stop Mode-A SSH tunnel first or ports 6379/27017/9005 will be busy.

$ErrorActionPreference = "Stop"
$Root = Split-Path -Parent $MyInvocation.MyCommand.Path
$DataRoot = Join-Path $Root "data"
$RedisDir = Join-Path $DataRoot "redis"
$MinioDir = Join-Path $DataRoot "minio"
$LogDir = Join-Path $Root "logs"
New-Item -ItemType Directory -Force -Path $RedisDir, $MinioDir, $LogDir | Out-Null

function Test-PortListen([int]$Port) {
  return [bool](Get-NetTCPConnection -LocalPort $Port -State Listen -EA SilentlyContinue)
}

function Assert-PortFree([int]$Port, [string]$Name) {
  if (Test-PortListen $Port) {
    $owners = (Get-NetTCPConnection -LocalPort $Port -State Listen -EA SilentlyContinue).OwningProcess | Select-Object -Unique
    $names = @()
    foreach ($procId in $owners) {
      try { $names += (Get-Process -Id $procId -EA SilentlyContinue).ProcessName } catch {}
    }
    throw ("Port {0} ({1}) busy: pid={2} process={3}. Kill SSH tunnel first." -f $Port, $Name, ($owners -join ','), ($names -join ','))
  }
}

Assert-PortFree 6379 "Redis"
Assert-PortFree 27017 "MongoDB"
Assert-PortFree 9005 "MinIO"
Assert-PortFree 9006 "MinIO-console"

$redis = (Get-Command redis-server -EA SilentlyContinue).Source
$mongod = (Get-Command mongod -EA SilentlyContinue).Source
$minio = (Get-Command minio -EA SilentlyContinue).Source
if (-not $redis) { throw "redis-server not found (scoop install redis)" }
if (-not $mongod) { throw "mongod not found (scoop install mongodb)" }
if (-not $minio) { throw "minio not found (scoop install minio)" }

# Scoop redis is a Cygwin build: config path must be /cygdrive/...
function ConvertTo-CygPath([string]$WinPath) {
  $full = (Resolve-Path $WinPath).Path
  if ($full -match '^([A-Za-z]):\\(.*)$') {
    return ('/cygdrive/{0}/{1}' -f $Matches[1].ToLower(), ($Matches[2] -replace '\\', '/'))
  }
  throw "Cannot convert path: $WinPath"
}
$redisConfWin = Join-Path $RedisDir "redis.local.conf"
$redisDirCyg = ConvertTo-CygPath $RedisDir
$redisConfCyg = "$redisDirCyg/redis.local.conf"
@"
bind 127.0.0.1
port 6379
dir $redisDirCyg
appendonly yes
maxmemory 256mb
maxmemory-policy allkeys-lru
"@ | Set-Content -Path $redisConfWin -Encoding ascii

Start-Process -FilePath $redis -ArgumentList @($redisConfCyg) -WindowStyle Hidden `
  -RedirectStandardOutput (Join-Path $LogDir "redis.out.log") `
  -RedirectStandardError (Join-Path $LogDir "redis.err.log")
Write-Host "started redis-server"

Start-Process -FilePath $mongod -WindowStyle Hidden `
  -RedirectStandardOutput (Join-Path $LogDir "mongo.out.log") `
  -RedirectStandardError (Join-Path $LogDir "mongo.err.log")
Write-Host "started mongod"

$env:MINIO_ROOT_USER = "minioadmin"
$env:MINIO_ROOT_PASSWORD = "minioadmin"
Start-Process -FilePath $minio -ArgumentList @(
  "server", $MinioDir, "--address", ":9005", "--console-address", ":9006"
) -WindowStyle Hidden `
  -RedirectStandardOutput (Join-Path $LogDir "minio.out.log") `
  -RedirectStandardError (Join-Path $LogDir "minio.err.log")
Write-Host "started minio"

$deadline = (Get-Date).AddMinutes(2)
while ((Get-Date) -lt $deadline) {
  $ok = (Test-PortListen 6379) -and (Test-PortListen 27017) -and (Test-PortListen 9005)
  if ($ok) { break }
  Start-Sleep -Seconds 1
}
if (-not (Test-PortListen 6379)) { throw "Redis not listening on 6379" }
if (-not (Test-PortListen 27017)) { throw "MongoDB not listening on 27017" }
if (-not (Test-PortListen 9005)) { throw "MinIO not listening on 9005" }

$mc = (Get-Command mc -EA SilentlyContinue).Source
if ($mc) {
  & $mc alias set local http://127.0.0.1:9005 minioadmin minioadmin | Out-Null
  & $mc mb -p local/training-take-out 2>$null | Out-Null
  & $mc anonymous set download local/training-take-out 2>$null | Out-Null
  Write-Host "minio bucket training-take-out ready"
}

Write-Host "OK native middleware up (ensure MySQL80 service is Running)"
Write-Host "  Redis  127.0.0.1:6379"
Write-Host "  Mongo  127.0.0.1:27017"
Write-Host "  MinIO  127.0.0.1:9005 (console 9006)"

# Stop Scoop-started Redis / Mongo / MinIO (does not stop MySQL80)
$ErrorActionPreference = "Continue"
Get-Process -Name redis-server,mongod,minio -EA SilentlyContinue | ForEach-Object {
  Write-Host ("stop {0} pid={1}" -f $_.ProcessName, $_.Id)
  Stop-Process -Id $_.Id -Force -EA SilentlyContinue
}
Write-Host "done (MySQL80 untouched)"

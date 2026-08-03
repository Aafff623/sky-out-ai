class AudioPlayer {
  private static instance: AudioPlayer;
  private audioContext: AudioContext | null = null;

  private constructor() {
    this.initAudioContext();
  }

  public static getInstance(): AudioPlayer {
    if (!AudioPlayer.instance) {
      AudioPlayer.instance = new AudioPlayer();
    }
    return AudioPlayer.instance;
  }

  private initAudioContext() {
    if (typeof window !== "undefined") {
      this.audioContext = new (window.AudioContext ||
        (window as any).webkitAudioContext)();
    }
  }

  public async play(src: string) {
    if (!this.audioContext) return;

    try {
      const response = await fetch(src);
      const arrayBuffer = await response.arrayBuffer();
      const audioBuffer = await this.audioContext.decodeAudioData(arrayBuffer);
      const source = this.audioContext.createBufferSource();
      source.buffer = audioBuffer;
      source.connect(this.audioContext.destination);
      source.start(0);
    } catch (error) {
      console.error("语音播放失败==>", error);
    }
  }
}

// 单例模式导出
export const audioPlayer = AudioPlayer.getInstance();

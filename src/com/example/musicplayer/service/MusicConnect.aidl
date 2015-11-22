package com.example.musicplayer.service;

interface MusicConnect {

   void play(int position);
   
   void pause();
   
   void restart();
   
   void stop();
   
   void playNext();
   
   void playPre();
   
   void seekTo(int rate);
   
   int getCurPosition();
   
   int getDuration();
   
   boolean isPlaying();
      
   void setPlayMode(int mode);
      
    int getPosition();
    
    String getMusicName();
    
    long getAlbumId();
    
    int getPlayMode();
}



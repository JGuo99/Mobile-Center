// IMediaService.aidl
package com.example.common;

// Declare any non-default types here with import statements

interface IMediaService {
    void endService();
    Bitmap imgIndex(int index);
    void musicIndex(int index);
    void play();
    void pause();
    void stop();
}
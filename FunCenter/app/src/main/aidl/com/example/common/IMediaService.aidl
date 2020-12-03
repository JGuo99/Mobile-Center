// IMediaService.aidl
package com.example.common;

// Declare any non-default types here with import statements

interface IMediaService {
//    void pause();
//    void play();
//    void stop();
//
//    //To open the Music
//    void open(in long [] list, int position);
//    int setImgIndex();
//    int getImgIndex();
    Bitmap imgIndex(int index);
//    For Testing
    int getPid();
}
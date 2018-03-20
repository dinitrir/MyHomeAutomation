package com.example.user.myhomeautomation;

public class Scenes {
    private String _sceneName;
    private String _lightLivingRoom;
    private String _lightKitchen;
    private String _lightOutside;
    private String _RGBlight;
    private String _gate;
    private String _shutter;
    private String _timeToActivate;
    private String _status;

    public Scenes(){}

    //Getters
    public String get_sceneName() {
        return _sceneName;
    }

    public String get_lightLivingRoom() {
        return _lightLivingRoom;
    }

    public String get_lightKitchen() {
        return _lightKitchen;
    }

    public String get_lightOutside() {
        return _lightOutside;
    }

    public String get_RGBlight() {
        return _RGBlight;
    }

    public String get_gate() {
        return _gate;
    }

    public String get_shutter() {
        return _shutter;
    }

    public String get_timeToActivate() {
        return _timeToActivate;
    }

    public String get_status() { return _status; }
    //Setters

    public void set_sceneName(String _sceneName) {
        this._sceneName = _sceneName;
    }

    public void set_lightLivingRoom(String _lightLivingRoom) {
        this._lightLivingRoom = _lightLivingRoom;
    }

    public void set_lightKitchen(String _lightKitchen) {
        this._lightKitchen = _lightKitchen;
    }

    public void set_lightOutside(String _lightOutside) {
        this._lightOutside = _lightOutside;
    }

    public void set_RGBlight(String _RGBlight) {
        this._RGBlight = _RGBlight;
    }

    public void set_gate(String _gate) {
        this._gate = _gate;
    }

    public void set_shutter(String _shutter) {
        this._shutter = _shutter;
    }

    public void set_timeToActivate(String _timeToActivate) {
        this._timeToActivate = _timeToActivate;
    }

    public void set_status(String _status) {
        this._status = _status;
    }
}

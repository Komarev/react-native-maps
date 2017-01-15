package com.airbnb.android.react.maps;

import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.common.MapBuilder;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.ViewGroupManager;
import com.facebook.react.uimanager.annotations.ReactProp;

import java.util.Map;

import javax.annotation.Nullable;

public class AirMapOverlayManager extends ViewGroupManager<AirMapOverlay> {

    public AirMapOverlayManager() {
    }

    @Override
    public String getName() {
        return "AIRMapOverlay";
    }

    @Override
    public AirMapOverlay createViewInstance(ThemedReactContext context) {
        return new AirMapOverlay(context);
    }

    @ReactProp(name = "bounds")
    public void setImageBounds(AirMapOverlay view, ReadableArray array) {
        view.setImageBounds(array.getArray(0), array.getArray(1));
    }

    @ReactProp(name = "name")
    public void setOverlayName(AirMapOverlay view, String name) {
        view.setOverlayName(name);
    }

    @ReactProp(name = "image")
    public void setImage(AirMapOverlay view, @Nullable String source) {
        view.setImage(source);
    }

    @ReactProp(name = "rotation", defaultFloat = 0.0f)
    public void setOverlayRotation(AirMapOverlay view, float rotation) {
        view.setRotation(rotation);
    }

    @ReactProp(name = "transparency", defaultFloat = 0.0f)
    public void setOverlayTransparency(AirMapOverlay view, float transparency) {
        view.setTransparency(transparency);
    }

    @Override
    @ReactProp(name = "zIndex", defaultFloat = 0.0f)
    public void setZIndex(AirMapOverlay view, float zIndex) {
      super.setZIndex(view, zIndex);
      int integerZIndex = Math.round(zIndex);
      view.setZIndex(integerZIndex);
    }

    @Override
    @Nullable
    public Map getExportedCustomDirectEventTypeConstants() {
        return MapBuilder.of("onPress", MapBuilder.of("registrationName", "onPress"));
    }
}

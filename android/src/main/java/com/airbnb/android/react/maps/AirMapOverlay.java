package com.airbnb.android.react.maps;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Animatable;
import android.net.Uri;

import com.facebook.common.references.CloseableReference;
import com.facebook.datasource.DataSource;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.drawee.controller.ControllerListener;
import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.DraweeHolder;
import com.facebook.imagepipeline.core.ImagePipeline;
import com.facebook.imagepipeline.image.CloseableImage;
import com.facebook.imagepipeline.image.CloseableStaticBitmap;
import com.facebook.imagepipeline.image.ImageInfo;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.facebook.react.bridge.ReadableArray;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.GroundOverlay;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import javax.annotation.Nullable;

public class AirMapOverlay extends AirMapFeature {

    private static final String TAG = "AirMapOverlay";

    private static float ANCHOR_X = 0.5f;
    private static float ANCHOR_Y = 0.5f;

    private GroundOverlayOptions overlayOptions;
    private GroundOverlay overlay;

    private LatLngBounds overlayBounds;
    private float transparency = 0.0f;
    private float rotation = 0.0f;
    private int zIndex = 0;
    private String name;

    private BitmapDescriptor imageBitmapDescriptor;
    private Bitmap imageBitmap;
    private final DraweeHolder<?> logoHolder;
    private DataSource<CloseableReference<CloseableImage>> dataSource;

    private final ControllerListener<ImageInfo> mLogoControllerListener =
            new BaseControllerListener<ImageInfo>() {
                @Override
                public void onFinalImageSet(
                        String id,
                        @Nullable final ImageInfo imageInfo,
                        @Nullable Animatable animatable) {
                    CloseableReference<CloseableImage> imageReference = null;
                    try {
                        imageReference = dataSource.getResult();
                        if (imageReference != null) {
                            CloseableImage image = imageReference.get();
                            if (image != null && image instanceof CloseableStaticBitmap) {
                                CloseableStaticBitmap closeableStaticBitmap = (CloseableStaticBitmap) image;
                                Bitmap bitmap = closeableStaticBitmap.getUnderlyingBitmap();
                                if (bitmap != null) {
                                    bitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
                                    imageBitmap = bitmap;
                                    imageBitmapDescriptor = BitmapDescriptorFactory.fromBitmap(bitmap);
                                }
                            }
                        }
                    } finally {
                        dataSource.close();
                        if (imageReference != null) {
                            CloseableReference.closeSafely(imageReference);
                        }
                    }
                    updateImage();
                }
            };

    public AirMapOverlay(Context context) {
        super(context);
        logoHolder = DraweeHolder.create(createDraweeHierarchy(), context);
        logoHolder.onAttach();
    }

    private GenericDraweeHierarchy createDraweeHierarchy() {
        return new GenericDraweeHierarchyBuilder(getResources())
                .setActualImageScaleType(ScalingUtils.ScaleType.FIT_CENTER)
                .setFadeDuration(0)
                .build();
    }

    public void setOverlayName(String name) {
        this.name = name;
    }

    public String getOverlayName() {
        return this.name;
    }

    public void setRotation(float rotation) {
        this.rotation = rotation;
        if (overlay != null) {
            overlay.setBearing(rotation);
        }
    }

    public void setZIndex(int zIndex) {
        this.zIndex = zIndex;
        if (overlay != null) {
            overlay.setZIndex(zIndex);
        }
    }

    public void setTransparency(float transparency) {
        this.transparency = transparency;
        if (overlay != null) {
            overlay.setTransparency(transparency);
        }
    }

    public GroundOverlayOptions getOverlayOptions() {
        if (overlayOptions == null) {
            overlayOptions = createOverlayOptions();
        }
        return overlayOptions;
    }

    @Override
    public Object getFeature() {
        return overlay;
    }

    @Override
    public void addToMap(GoogleMap map) {
        overlay = map.addGroundOverlay(getOverlayOptions());
    }

    @Override
    public void removeFromMap(GoogleMap map) {
        overlay.remove();
        overlay = null;
    }

    private GroundOverlayOptions createOverlayOptions() {
        return new GroundOverlayOptions()
                .positionFromBounds(overlayBounds)
                .anchor(ANCHOR_X, ANCHOR_Y)
                .bearing(rotation)
                .zIndex(zIndex)
                .transparency(transparency)
                .clickable(true)
                .image(imageBitmapDescriptor);
    }

    public void updateImage() {
        if (overlay != null) {
            overlay.setImage(imageBitmapDescriptor);
        }
    }

    public void setImageBounds(ReadableArray topLeft, ReadableArray bottomRight) {
        LatLng topLeftCoord = new LatLng(topLeft.getDouble(0), topLeft.getDouble(1));
        LatLng bottomRightCoord = new LatLng(bottomRight.getDouble(0), bottomRight.getDouble(1));
        overlayBounds = new LatLngBounds.Builder()
                .include(topLeftCoord)
                .include(bottomRightCoord)
                .build();
        if (overlay != null) {
            overlay.setPositionFromBounds(overlayBounds);
        }
    }

    public void setImage(String uri) {
        if (uri == null) {
            return;
        }

        if (uri.startsWith("http://") || uri.startsWith("https://") || uri.startsWith("file://")) {
            ImageRequest imageRequest = ImageRequestBuilder
                    .newBuilderWithSource(Uri.parse(uri))
                    .build();

            ImagePipeline imagePipeline = Fresco.getImagePipeline();
            dataSource = imagePipeline.fetchDecodedImage(imageRequest, this);
            DraweeController controller = Fresco.newDraweeControllerBuilder()
                    .setImageRequest(imageRequest)
                    .setControllerListener(mLogoControllerListener)
                    .setOldController(logoHolder.getController())
                    .build();
            logoHolder.setController(controller);
            if (imageBitmapDescriptor == null) {
                imageBitmapDescriptor = BitmapDescriptorFactory.fromBitmap(createDrawable());
            }
        } else {
            imageBitmapDescriptor = getBitmapDescriptorByName(uri);
            updateImage();
        }
    }

    private Bitmap createDrawable() {
        this.buildDrawingCache();
        Bitmap bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        this.draw(canvas);

        return bitmap;
    }

    private int getDrawableResourceByName(String name) {
        int resId = getResources().getIdentifier(name, "mipmap", getContext().getPackageName());
        return resId != 0 ? resId : getResources().getIdentifier(name, "drawable", getContext().getPackageName());
    }

    private BitmapDescriptor getBitmapDescriptorByName(String name) {
        return BitmapDescriptorFactory.fromResource(getDrawableResourceByName(name));
    }

}

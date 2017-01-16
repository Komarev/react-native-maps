//
//  AIRMapOverlayManager.m
//  AirMapsExplorer
//
//  Created by Alexander Perepelitsyn on 1/15/17.
//  Copyright © 2017 Facebook. All rights reserved.
//

#import "AIRMapOverlayManager.h"

#import <React/RCTConvert+CoreLocation.h>
#import <React/RCTUIManager.h>
#import <React/UIView+React.h>
#import "AIRMapOverlay.h"

@interface AIRMapOverlayManager () <MKMapViewDelegate>

@end

@implementation AIRMapOverlayManager

RCT_EXPORT_MODULE()

- (UIView *)view
{
  AIRMapOverlay *overlay = [AIRMapOverlay new];
  overlay.bridge = self.bridge;
  return overlay;
}

RCT_EXPORT_VIEW_PROPERTY(name, NSString)
RCT_REMAP_VIEW_PROPERTY(bounds, boundsRect, NSArray)
RCT_REMAP_VIEW_PROPERTY(image, imageSrc, NSString)
RCT_EXPORT_VIEW_PROPERTY(rotation, NSInteger)
RCT_EXPORT_VIEW_PROPERTY(transparency, CGFloat)
RCT_EXPORT_VIEW_PROPERTY(zIndex, NSInteger)
RCT_EXPORT_VIEW_PROPERTY(onPress, RCTBubblingEventBlock)

@end

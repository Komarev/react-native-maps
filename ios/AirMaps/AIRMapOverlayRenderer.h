//
//  AIRMapOverlayView.h
//  AirMapsExplorer
//
//  Created by Alexander Perepelitsyn on 1/16/17.
//  Copyright © 2017 Facebook. All rights reserved.
//

#import <MapKit/MapKit.h>

@interface AIRMapOverlayRenderer : MKOverlayRenderer

@property (nonatomic, assign) NSInteger rotation;
@property (nonatomic, assign) CGFloat transparency;

@end

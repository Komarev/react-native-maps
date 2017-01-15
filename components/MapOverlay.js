import React, { PropTypes, Component } from 'react';
import {
  View,
  StyleSheet,
  Platform,
  Animated,
} from 'react-native';

import resolveAssetSource from 'react-native/Libraries/Image/resolveAssetSource';
import decorateMapComponent, {
  SUPPORTED,
  USES_DEFAULT_IMPLEMENTATION,
} from './decorateMapComponent';

class MapOverlay extends Component {

  static viewConfig = {
    uiViewClassName: 'AIR<provider>MapOverlay',
    validAttributes: {
      image: true,
    },
  };

  static defaultProps = {
    name: '',
    rotation: 0,
    zIndex: 0,
    transparency: 0,
    onPress: () => {},
  };

  static propTypes = {
    ...View.propTypes,
    // A custom image to be used as overlay. Only local image resources are allowed to be used.
    image: PropTypes.any.isRequired,
    // Top left and bottom right coordinates for overlay
    bounds: PropTypes.arrayOf(PropTypes.array.isRequired).isRequired,
    // A name for the image overlay
    name: PropTypes.string,
    // A number of degrees from north to rotate the image clockwise
    rotation: PropTypes.number,
    // A number indicating the render order of the image
    zIndex: PropTypes.number,
    // A decimal from 0 to 1 in indicating the opaqueness of the overlay 1 = completely transparent.
    transparency: PropTypes.number,
    // Callback that is called when the user presses on the overlay
    onPress: PropTypes.func,
  };

  constructor(props) {
    super(props);
  }

  render() {
    let image;
    if (this.props.image) {
      image = resolveAssetSource(this.props.image) || {};
      image = image.uri;
    }

    const AIRMapOverlay = this.getAirComponent();

    return (
      <AIRMapOverlay
        {...this.props}
        image={image}
        style={[styles.overlay, this.props.style]}
      />
    );
  }
}

const styles = StyleSheet.create({
  overlay: {
    position: 'absolute',
    backgroundColor: 'transparent',
  },
});

MapOverlay.Animated = Animated.createAnimatedComponent(MapOverlay);

module.exports = decorateMapComponent(MapOverlay, {
  componentType: 'Overlay',
  providers: {
    google: {
      ios: SUPPORTED,
      android: USES_DEFAULT_IMPLEMENTATION,
    },
  },
});

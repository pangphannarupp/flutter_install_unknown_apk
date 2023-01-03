import Flutter
import UIKit

public class SwiftFlutterInstallUnknownApkPlugin: NSObject, FlutterPlugin {
  public static func register(with registrar: FlutterPluginRegistrar) {
    let channel = FlutterMethodChannel(name: "flutter_install_unknown_apk", binaryMessenger: registrar.messenger())
    let instance = SwiftFlutterInstallUnknownApkPlugin()
    registrar.addMethodCallDelegate(instance, channel: channel)
  }

  public func handle(_ call: FlutterMethodCall, result: @escaping FlutterResult) {
    result("iOS " + UIDevice.current.systemVersion)
  }
}

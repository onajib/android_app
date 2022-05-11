package com.example.native_app

import android.os.Bundle
import com.example.native_app.BuildConfig.CREATE_ACCOUNT_URL
import com.example.native_app.BuildConfig.RESET_PASSWORD_URL
import com.example.native_app.databinding.ActivityMainBinding
import com.example.native_app.utils.*
import io.flutter.embedding.android.FlutterActivity
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.embedding.engine.FlutterEngineCache
import io.flutter.embedding.engine.dart.DartExecutor
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugins.GeneratedPluginRegistrant

class MainActivity : FlutterActivity() {

    private lateinit var binding: ActivityMainBinding

    lateinit var flutterCustomEngine: FlutterEngine

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        warmUpFlutterEngine()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initListeners()
    }

    private fun warmUpFlutterEngine() {
        flutterCustomEngine = FlutterEngine(this)
        flutterCustomEngine.navigationChannel.setInitialRoute(FLUTTER_MODULE_AUTH_ROUTE)
        flutterCustomEngine.dartExecutor.executeDartEntrypoint(
            DartExecutor.DartEntrypoint.createDefault()
        )
        FlutterEngineCache.getInstance().put(FLUTTER_ENGINE_ID, flutterCustomEngine)
    }

    private fun initListeners() {
        initUiListeners()
        initFlutterListener()
    }

    private fun initUiListeners() {
        binding.bGoToLogin.setOnClickListener {
            startActivity(
                withCachedEngine(FLUTTER_ENGINE_ID)
                    .build(context)
            )
        }
    }

    private fun initFlutterListener() {
        GeneratedPluginRegistrant.registerWith(flutterCustomEngine)
        MethodChannel(
            flutterCustomEngine.dartExecutor,
            NATIVE_CHANNEL
        ).setMethodCallHandler { call, result ->
            handleClientMethodCall(call, result)
        }
    }

    private fun handleClientMethodCall(
        call: MethodCall,
        result: MethodChannel.Result
    ) {
        when (call.method) {
            TOKEN_TO_NATIVE -> {
                with(call.arguments as String) {
                    handleSuccessfulLogin(this)
                    result.success(this)
                }
            }
            GET_ACCOUNT_CREATION_URL -> result.success(CREATE_ACCOUNT_URL)
            GET_RESET_PASSWORD_URL -> result.success(RESET_PASSWORD_URL)
            else -> result.notImplemented()
        }
    }

    private fun handleSuccessfulLogin(token: String?) {
        binding.bGoToLogin.apply {
            text = getString(R.string.login_successful)
            isEnabled = false
        }
        binding.tvToken.text = token
    }
}
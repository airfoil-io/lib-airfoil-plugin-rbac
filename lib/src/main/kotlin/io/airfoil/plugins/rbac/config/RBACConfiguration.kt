package io.airfoil.plugins.rbac.config

import io.airfoil.common.extension.stringListOrEmpty
import io.airfoil.common.extension.stringValueOrError
import io.airfoil.plugins.rbac.data.domain.dto.Subject
import io.ktor.server.config.ApplicationConfig

class RBACConfiguration {
    lateinit var classPath: String
    lateinit var subjects: Set<Subject>

    companion object {
        const val CONFIG_KEY = "rbac"

        fun load(
            config: ApplicationConfig,
            configKey: String = CONFIG_KEY,
        ): RBACConfiguration = config.config(configKey).let { cfg ->
            RBACConfiguration().also {
                it.classPath = cfg.stringValueOrError("classPath", "Property $configKey.classPath not found")
                it.subjects = cfg.stringListOrEmpty("subjects").toSet()
            }
        }
    }
}

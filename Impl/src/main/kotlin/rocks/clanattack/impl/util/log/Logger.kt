package rocks.clanattack.impl.util.log

import rocks.clanattack.entry.service.ServiceImplementation
import rocks.clanattack.util.log.Logger as Interface
import java.util.logging.Level
import java.util.logging.Logger as JavaLogger

class Logger(private val logger: JavaLogger) : ServiceImplementation(), Interface {

    override fun info(message: String, throwable: Throwable?, printStackTrace: Boolean) {
        if (throwable != null || printStackTrace) logger.log(Level.INFO, message, throwable ?: Throwable())
        else logger.info(message)
    }

    override fun warn(message: String, throwable: Throwable?, printStackTrace: Boolean) {
        if (throwable != null || printStackTrace) logger.log(Level.WARNING, message, throwable ?: Throwable())
        else logger.warning(message)
    }

    override fun error(message: String, throwable: Throwable?, printStackTrace: Boolean) {
        if (throwable != null || printStackTrace) logger.log(Level.SEVERE, message, throwable ?: Throwable())
        else logger.severe(message)
    }

}
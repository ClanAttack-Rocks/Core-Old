package rocks.clanattack.impl.util.log

import rocks.clanattack.entry.service.ServiceImplementation
import rocks.clanattack.util.log.Logger
import java.util.logging.Level
import java.util.logging.Logger as JavaLogger

class LoggerImpl(private val logger: JavaLogger) : ServiceImplementation(), Logger {

    override fun info(message: String) = logger.info(message)

    override fun warn(message: String) = logger.warning(message)

    override fun error(message: String, throwable: Throwable?) {
        if (throwable == null) logger.severe(message)
        else logger.log(Level.SEVERE, message, throwable)
    }

}
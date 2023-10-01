package rocks.clanattack.impl.util.log

import rocks.clanattack.entry.service.ServiceImplementation
import rocks.clanattack.util.log.Logger as Interface
import java.util.logging.Level
import java.util.logging.Logger as JavaLogger

class Logger(private val logger: JavaLogger) : ServiceImplementation(), Interface {

    override fun info(message: String, throwable: Throwable?) {
        if (throwable == null) logger.info(message)
        else logger.log(Level.INFO, message, throwable)
    }

    override fun warn(message: String, throwable: Throwable?) {
        if (throwable == null) logger.warning(message)
        else logger.log(Level.WARNING, message, throwable)
    }

    override fun error(message: String, throwable: Throwable?) {
        if (throwable == null) logger.severe(message)
        else logger.log(Level.SEVERE, message, throwable)
    }

}
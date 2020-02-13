import logging.config

logging.config.fileConfig('./../conf/local/logging.conf')

# create logger
logger = logging.getLogger('dream_logger')

# 'application' code
logger.debug('debug message')
logger.info('info message')
logger.warning('%s before you %s', 'Look', 'leap!')
logger.error('error message')
logger.critical('critical message')

import os
basedir = os.path.abspath(os.path.dirname(__file__))

class Config:
    BASEDIR = basedir
    DATA_DIR = os.path.join(basedir, 'data')
    PLOT_DIR = os.path.join(basedir, 'images')

class DevelopmentConfig(Config):
    SQLALCHEMY_DATABASE_URI = \
        'sqlite:///' +  os.path.join(basedir, 'asaexone-dev.sqlite')

config = {
    'default': DevelopmentConfig,
    'development': DevelopmentConfig
}

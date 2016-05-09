from .asaextwodb import db
from .asaextwostats import ASAExTwoStats
from config import config

def create_app(config_name):
    app = ASAExTwoStats(config[config_name])
    db.init_app(app)

    return app

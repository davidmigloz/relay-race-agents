import re
from sqlalchemy import create_engine
from sqlalchemy.orm import sessionmaker
from sqlalchemy.ext.declarative import declarative_base

class ASAExTwoDb:
    
    # Engine
    __engine = None
    
    # Database session
    session = None
   
    # Base declarative
    Model = None
    
    def __init__(self):
        self.Model = self.__make_declarative_base()
    
    def init_app(self, app):
        if re.search(r"sqlite:////", app.config.SQLALCHEMY_DATABASE_URI):
            target_uri = app.config.SQLALCHEMY_DATABASE_URI
        else:
            uri_parts = app.config.SQLALCHEMY_DATABASE_URI.split('://')
            if uri_parts[0] == 'sqlite':
                target_uri = uri_parts[0] + ':///' + app.config.BASEDIR + '/storage' + uri_parts[1]
            else:
                target_uri = app.config.SQLALCHEMY_DATABASE_URI
        
        self.__engine = create_engine(target_uri)
        self.Model.metadata.create_all(self.__engine)
        self.Model.metadata.bind = self.__engine
        self.session = sessionmaker(bind=self.__engine)()
    
    def __make_declarative_base(self):
        base = declarative_base()
        
        return base

# Define global database object.
db = ASAExTwoDb()

import os
import json
import glob
import pandas as pd
import matplotlib
import matplotlib.pyplot as plt
import numpy as np

class ASAExTwoStats:

    # Settings loaded from configuration file.
    config = None

    def __init__(self, config):
        # Make app config available through class property.
        self.config = config()
        matplotlib.style.use('ggplot')
    
    def combine(self):
        pass
    
    def run_calcs(self):
        pass

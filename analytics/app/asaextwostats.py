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

    data_file = None

    def __init__(self, config):
        # Make app config available through class property.
        self.config = config()
        self.data_file = self.config.DATA_DIR + '/combined.json'
        matplotlib.style.use('ggplot')

    def combine(self):
        combined = {'runs': []}
        files = glob.glob(self.config.DATA_DIR + '/*.json')
        for file in files:
            if 'combined' not in file:
                with open(file) as data_file:
                    combined['runs'].append(
                        json.loads(data_file.readline()[1:-1]))
        with open(self.data_file, 'w') as json_file:
            json.dump(combined, json_file)
            print('Data written to file.')

    def run_calcs(self):
        with open(self.data_file, 'r') as file:
            data = json.load(file)
            df = pd.io.json.json_normalize(data['runs'])
            df['runtime'] = (df['runtime']) / 1000

            df_macs = (df[(df['laps'] == 10)
                          & (df['teams'] == 3)]).sort_values(by='machines')

            df_teams = (df[(df['machines'] == 3)
                           & (df['laps'] == 10)]).sort_values(by='teams')

            df_laps = (df[(df['machines'] == 3)
                          & (df['teams'] == 3)]).sort_values(by='laps')

            self.plot_data(df_macs, 'machines', 'machines', 'runtime',
                           'line', 'Number of Machines', 'Runtime (s)', [2, 8], [0, 150])
            self.plot_data(df_teams, 'teams', 'teams', 'runtime',
                           'line', 'Number of Teams', 'Runtime (s)', [0, 25], [0, 35])
            self.plot_data(df_laps, 'laps', 'laps', 'runtime',
                           'line', 'Number of Laps', 'Runtime (s)', [0, 16], [0, 10])

    def plot_data(self, df, name, x, y, kind, xl, yl, xlim, ylim):
        plt.figure()
        plot = df.plot(kind=kind, x=x, y=y)
        plot.set_xlabel(xl)
        plot.set_xlim(xlim)
        plot.set_ylabel(yl)
        plot.set_ylim(ylim)
        plt.savefig(os.path.join(self.config.PLOT_DIR, name + '.eps'))

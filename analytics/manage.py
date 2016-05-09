import os
import argparse
from app import create_app

if __name__ == '__main__':
    parser = argparse.ArgumentParser(
        description="Compute asaextwo statistics."
    )
    
    megroup = parser.add_mutually_exclusive_group()
    
    megroup.add_argument('--test',
        action='store_true',
        help='Run unit tests.'
    )
    
    megroup.add_argument('--combine',
        action='store_true',
        help='Combine data from all files.'
    )
    
    megroup.add_argument('--run',
        action='store_true',
        help='Run misc calcs'
    )
    
    args = parser.parse_args()
    
    if args.test:
        # Run the unit tests.
        import unittest
        tests = unittest.TestLoader().discover('tests')
        unittest.TextTestRunner(verbosity=2).run(tests)
    elif args.combine:
        # Combine and dump data from input data files.
        # Data is dumped to CSV format.
        app = create_app('default')
        app.combine()
    elif args.run:
        # Combine and dump data from input data files.
        # Data is dumped to CSV format.
        app = create_app('default')
        app.run_calcs()
    else:
        print("An option must be specified. To view options, use:\n\n$ python manage.py --help\n")

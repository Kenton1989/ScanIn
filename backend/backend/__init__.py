import os
import sys

curDir = os.path.dirname(os.path.realpath(__file__))
if curDir not in sys.path:  # add current dir to paths
    sys.path.append(curDir)
    print("added", curDir, "into search path")

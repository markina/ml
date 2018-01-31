import numpy as np
import matplotlib.pyplot as plt

mean1 = [0, 0]
cov1 = [[1, 0], [0, 100]]  # diagonal covariance

x1, y1 = np.random.multivariate_normal(
    [0, 0],
    [[1, 0], [0, 100]],
    500).T

plt.plot(x1, y1, 'k.')

x2, y2 = np.random.multivariate_normal(
    [7, 7],
    [[1, 0], [0, 100]],
    500).T
plt.plot(x2, y2, 'k.')

xx1, yy1 = x1[0], y1[0]
xx2, yy2 = x2[0], y2[0]

plt.plot([xx1], [yy1], 'bo')
plt.plot([xx2], [yy2], 'ro')

plt.show()

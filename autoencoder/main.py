import tensorflow as tf
import tensorflow.examples.tutorials.mnist.input_data as input_data

import numpy as np
import math
import matplotlib.pyplot as plt


def autoencoder(input_shape=[None, 784],
                n_filters=[1, 10, 10, 10],
                filter_sizes=[3, 3, 3, 3]):
    # input
    x = tf.placeholder(
        tf.float32, input_shape, name='x')

    # ensure second is converted to square tensor.
    if len(x.get_shape()) == 2:
        x_dim = np.sqrt(x.get_shape().as_list()[1])
        if x_dim != int(x_dim):
            raise ValueError('Unsupported input dimensions')
        x_dim = int(x_dim)
        x_tensor = tf.reshape(
            x, [-1, x_dim, x_dim, n_filters[0]])
    elif len(x.get_shape()) == 4:
        x_tensor = x
    else:
        raise ValueError('Unsupported input dimensions')
    current_input = x_tensor

    # build the encoder
    encoder = []
    shapes = []
    for layer_i, n_output in enumerate(n_filters[1:]):
        n_input = current_input.get_shape().as_list()[3]
        shapes.append(current_input.get_shape().as_list())
        w = tf.Variable(
            tf.random_uniform([
                filter_sizes[layer_i],
                filter_sizes[layer_i],
                n_input, n_output],
                -1.0 / math.sqrt(n_input),
                1.0 / math.sqrt(n_input)))
        b = tf.Variable(tf.zeros([n_output]))
        encoder.append(w)
        output = leak(
            tf.add(
                tf.nn.conv2d(current_input, w, strides=[1, 2, 2, 1], padding='SAME'),
                b
            ))
        current_input = output

    # store the latent representation
    z = current_input
    encoder.reverse()
    shapes.reverse()

    # build the decoder using the same weights
    for layer_i, shape in enumerate(shapes):
        w = encoder[layer_i]
        b = tf.Variable(tf.zeros([w.get_shape().as_list()[2]]))
        output = leak(
            tf.add(
                tf.nn.conv2d_transpose(
                    current_input, w,
                    tf.stack([tf.shape(x)[0], shape[1], shape[2], shape[3]]),
                    strides=[1, 2, 2, 1], padding='SAME'),
                b
            ))
        current_input = output

    y = current_input
    # loos function measures pixel-wise difference
    cost = tf.reduce_sum(tf.square(y - x_tensor))

    return {'x': x, 'z': z, 'y': y, 'cost': cost}


def test():
    # test the convolutional autoencoder using MNIST

    # load MNIST
    mnist = input_data.read_data_sets('MNIST_data', one_hot=True)
    mean_img = np.mean(mnist.train.images, axis=0)
    ae = autoencoder()

    learning_rate = 0.01
    optimizer = tf.train.AdamOptimizer(learning_rate).minimize(ae['cost'])

    sess = tf.Session()
    sess.run(tf.global_variables_initializer())

    # fit all training data
    batch_size = 100
    n_epochs = 10
    for epoch_i in range(n_epochs):
        for batch_i in range(mnist.train.num_examples // batch_size):
            batch_xs, _ = mnist.train.next_batch(batch_size)
            train = np.array([img - mean_img for img in batch_xs])
            sess.run(optimizer, feed_dict={ae['x']: train})
        print(epoch_i, sess.run(ae['cost'], feed_dict={ae['x']: train}))

    # plot example reconstructions
    n_examples = 10
    test_xs, _ = mnist.test.next_batch(n_examples)
    test_xs_norm = np.array([img - mean_img for img in test_xs])
    recon = sess.run(ae['y'], feed_dict={ae['x']: test_xs_norm})
    print(recon.shape)
    fig, axs = plt.subplots(2, n_examples, figsize=(10, 2))
    for example_i in range(n_examples):
        axs[0][example_i].imshow(
            np.reshape(test_xs[example_i, :], (28, 28)))
        axs[1][example_i].imshow(
            np.reshape(
                np.reshape(recon[example_i, ...], (784,)) + mean_img,
                (28, 28)))
    fig.show()
    plt.draw()
    plt.waitforbuttonpress()


def leak(x):
    p = 0.2
    with tf.variable_scope("leak"):
        f1 = 0.5 * (1 + p)
        f2 = 0.5 * (1 - p)
        return f1 * x + f2 * abs(x)


if __name__ == '__main__':
    test()

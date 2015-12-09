ImageCompare
=====

What is it?
-----

A utility for comparing visual similarity of images!

Building
-----

I just used NetBeans to build it. The Main class can be run from the command line as shown in this readme.

Usage
-----

### Commands Overview

Compare, hash, or test (TODO)

```markdown
>java -cp "lib/*;build/classes" Main --help
usage: ic [-h] {compare,hash,test} ...

Calculate the amount of visual similarity between two or more images.

positional arguments:
  {compare,hash,test}

optional arguments:
  -h, --help             show this help message and exit
```

### Compare

Compare two or more images, or a reference hash to one or more images.
Calculates and prints the hashes and multi-resolution similarity between reference and specified images.

```markdown
>java -cp "lib/*;build/classes" Main compare --help
usage: ic compare [-h] [-d DEGREE] (-R REFERENCE_HASH | -r REFERENCE)
          image [image ...]

positional arguments:
  image                  Image files to compare to reference.

optional arguments:
  -h, --help             show this help message and exit
  -d DEGREE, --degree DEGREE
                         Sets  the   degree   of   the   hash   (number  of
                         subdivisions, resolution count)

Reference:
  -R REFERENCE_HASH, --reference-hash REFERENCE_HASH
                         Supply the reference hash  rather than calculating
                         it.
  -r REFERENCE, --reference REFERENCE
                         Reference image file.
```

#### Example

```markdown
>java -cp "lib/*;build/classes" Main compare -r resources/buns1.png resources/buns2.png
 Reference: YnF7fpOwYmdrQktPa4CYfo6jY2RmRUpJco2pY3ODRk1VIiotYXSCb3uELztCSVde
   Image 0: YHR6fpOwYmdrQktPa4CYfo6jY2RmRUpJco2pY3ODRk1VIiotYXSCb3uELztCSVde
Similarity: 99.96%

>java -cp "lib/*;build/classes" Main compare -r resources/banana1.jpg resources/banana2.jpg
 Reference: psTNa4eWbYCKepWmlKq0MUNQKTxIhaiycHqmHjBAOk5cipa2PUGfV1G5Pj+fNDeE
   Image 0: sc7VaYWUh5qjfZmqepWeM0ZULEFPkLK8X2uBHy87N0pYk6S0SlOkQEeNSVKWUVWl
Similarity: 86.23%

>java -cp "lib/*;build/classes" Main compare -r resources/buns1.png resources/banana1.jpg
 Reference: YnF7fpOwYmdrQktPa4CYfo6jY2RmRUpJco2pY3ODRk1VIiotYXSCb3uELztCSVde
   Image 0: psTNa4eWbYCKepWmlKq0MUNQKTxIhaiycHqmHjBAOk5cipa2PUGfV1G5Pj+fNDeE
Similarity: 56.93%
```

### Hash

Calculate and print the color hash of an image, encoded in base 64.

```markdown
>java -cp "lib/*;build/classes" Main hash --help
usage: ic hash [-h] [-d DEGREE] image

positional arguments:
  image                  Image file to hash.

optional arguments:
  -h, --help             show this help message and exit
  -d DEGREE, --degree DEGREE
                         Sets  the   degree   of   the   hash   (number  of
                         subdivisions, resolution count)
```

#### Example
```markdown
>java -cp "lib/*;build/classes" Main hash resources/buns1.png
YnF7fpOwYmdrQktPa4CYfo6jY2RmRUpJco2pY3ODRk1VIiotYXSCb3uELztCSVde

>java -cp "lib/*;build/classes" Main hash resources/banana1.jpg
psTNa4eWbYCKepWmlKq0MUNQKTxIhaiycHqmHjBAOk5cipa2PUGfV1G5Pj+fNDeE
```

### Credits

Developed by Vincent Brubaker Gianakos

Moral support from Eang and Ty ☺
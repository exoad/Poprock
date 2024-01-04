<div align="center">
<img src="Repo/Poprock_Logo.png" width=76 />
<br/>
<h1>
Poprock
</h1>
</div>




> [!WARNING]
> Currently, this is a work in progress!

**Poprock** is a resilient and lightweight framework designed to harness the extensive Java ecosystem for the swift and efficient development of desktop applications. Beyond constructing basic GUIs, the Poprock framework seamlessly extends its utility to creating straightforward games, music players, and other applications. In addition to desktop applications, Poprock can function as a standalone library for diverse tasks such as mathematical computations, audio processing, image processing, and more.

Furthermore, it is licensed under the free-to-use `BSD-4` license.

## Features

* **Graphics Toolkit** with hardware (OpenGL) & software rendering 
* **Native Audio engine** using ALSA, Jack, Pulse
* **Tailwind** - real-time signal processing (WIP)
* Fast **Linear Algebra** lib
* Thread safe caching libraries
* **Image Processing**
* **Prismix** - color & Theme Processor
* **Txfyr** - a simple texture atlas library
* Seamless native SYS calls for Windows, OSX, and Linux*
* Lua SPI integrations

... and more to come


## Design

This section documents quirks and other features that are used in the "code" of Poprock

### "Named Construction / Factory"

Named construction leverages *method chaining* to produce objects that need much information (parameters) to be computed. Here is a look at the differences between traditional Setters (imperative) and using Named Construction (as adopted in Poprock):

<table>
  <tr>
    <th><strong>Imperative</strong> (<code>set-</code>)</th>
    <th><strong>Named</strong> (<code>with-</code>)</th>
  </tr>
  <tr>
    <td>

```java
JFrame jf=new JFrame();
jf.setTitle("Foo");
jf.setIconImage(ImageIO.read("icon.png"));
Dimension dim=new Dimension(300,400);
jf.setSize(dim);
jf.setPreferredSize(dim);
jf.setMaximumSize(dim);
jf.setLocationRelativeTo(null);
jf.pack();
jf.setVisible(true)
```

  </td>
    <td>

```java
UIWindow.make()
        .withTitle("Foo")
        .withIcon(
            AssetsService.fetchImage("icon.png")
                         .as(ImageRasterizer.Type.BUFFERED_IMAGE)
        )
        .withDim(300,400)
        .withMaxDim(300,400)
        .withLocation(Alignment.CENTER)
        .run();
```
      
  </td>
  </tr>
</table>

> [!NOTE]
> The formatting is also part of the "Named" Construction pattern and is critical to making the code much more readable.

/*
 * This file is part of Zapper, licensed under the MIT License.
 *
 *  Copyright (c) Revxrsal <reflxction.github@gmail.com>
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
 */
package revxrsal.zapper.classloader;

import org.jetbrains.annotations.NotNull;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.Collection;
import java.util.List;

import static revxrsal.zapper.classloader.UnsafeUtil.getField;
import static revxrsal.zapper.classloader.UnsafeUtil.isJava8;

/**
 * An implementation that uses sun.misc.Unsafe to inject URLs
 */
final class ByUnsafe extends URLClassLoaderWrapper {

    private final Collection<URL> unopenedURLs;
    private final List<URL> pathURLs;

    public ByUnsafe(@NotNull URLClassLoader loader) {
        Object ucp = getField(loader, "ucp", URLClassLoader.class);
        unopenedURLs = getField(ucp, isJava8() ? "urls" : "unopenedUrls", ucp.getClass());
        pathURLs = getField(ucp, "path", ucp.getClass());
    }

    public void addURL(@NotNull URL url) {
        unopenedURLs.add(url);
        pathURLs.add(url);
    }
}
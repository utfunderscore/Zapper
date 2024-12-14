/*
 * This file is part of WorldCleaner, licensed under the MIT License.
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
package revxrsal.zapper;

import org.jetbrains.annotations.NotNull;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Represents a Maven repository with a URL
 */
public class Repository {

    private static final Repository MAVEN_CENTRAL = new Repository("https://repo1.maven.org/maven2/");
    private static final Repository JITPACK = new Repository("https://jitpack.io/");
    private static final Repository MINECRAFT = new Repository("https://libraries.minecraft.net/");
    private static final Repository PAPER = new Repository("https://papermc.io/repo/repository/maven-public/");

    public static @NotNull Repository mavenCentral() {
        return MAVEN_CENTRAL;
    }

    public static @NotNull Repository jitpack() {
        return JITPACK;
    }

    public static @NotNull Repository minecraft() {
        return MINECRAFT;
    }

    public static @NotNull Repository paper() {
        return PAPER;
    }

    public static @NotNull Repository maven(@NotNull String url) {
        return new Repository(url);
    }

    private final String repoURL;

    private Repository(@NotNull String repoURL) {
        if (repoURL.charAt(repoURL.length() - 1) != '/')
            repoURL += '/';
        this.repoURL = repoURL;
    }

    public String getRepositoryURL() {
        return repoURL;
    }

    public URL resolve(Dependency dependency) {
        try {
            return new URL(repoURL + dependency.getMavenPath());
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

}

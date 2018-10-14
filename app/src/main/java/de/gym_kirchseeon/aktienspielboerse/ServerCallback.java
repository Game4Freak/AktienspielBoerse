package de.gym_kirchseeon.aktienspielboerse;

/**
 * Used to use the description from WIkipedia Downloader.
 */
public interface ServerCallback {
    void onSuccess(String extract, String URL);

    void onError();
}

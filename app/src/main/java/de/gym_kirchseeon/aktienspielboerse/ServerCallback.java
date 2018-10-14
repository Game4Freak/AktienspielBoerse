package de.gym_kirchseeon.aktienspielboerse;

/**
 * Used to use the description from WIkipedia Downloader.
 */
public interface ServerCallback {
    void onSuccess(String extract);

    void onError();
}

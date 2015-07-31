package com.kidgeniushq.interfaces;

import net.orange_box.storebox.annotations.method.KeyByString;

import java.util.Set;

/**
 * Created by benjamin.harvey on 7/31/15.
 */
public interface MyPreferences {

    @KeyByString("igposts")
    Set<String> getIGPosts();

    @KeyByString("igposts")
    void setIGPosts(Set<String> posts);

}
# AudioTrimmer
Audio trimming library for Android (No processing)

This library contains only the UI and logic for the trimmer and not the actual processing of the files . You can use FFmpeg or any other libraries for that.
A sample app is yet to be added but here is the basic implementation of the trimmer as a view.

Example to use with xml - 

```
<androidx.constraintlayout.widget.ConstraintLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_centerInParent="true"
    android:layout_height="wrap_content"
    xmlns:app="http://schemas.android.com/apk/res-auto">
    <com.pro.audiotrimmer.AudioTrimmerView
        android:id="@+id/audioTrimmerView"
        android:layout_width="match_parent"
        app:layout_constraintBottom_toBottomOf="parent"
        android:paddingTop="@dimen/twenty_dp"
        android:layout_height="wrap_content"
        app:atv_window_border_color="@color/bottom_tab"
        app:atv_window_border_width="1dp"
        app:atv_overlay_color="@color/overlay"
        android:background="@color/background"
        app:atv_window_bar_width="15dp"
        app:atv_window_left_bar="@drawable/handle_left"
        app:atv_window_right_bar="@drawable/handle_right"/>
</androidx.constraintlayout.widget.ConstraintLayout>
```

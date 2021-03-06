package com.huyn.demogroup.photo;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.huyn.demogroup.R;

import java.util.ArrayList;
import java.util.List;

import static android.widget.Toast.LENGTH_LONG;

public class PhotoPickerActivity extends AppCompatActivity {

  private PhotoPickerFragment pickerFragment;
//  private ImagePagerFragment imagePagerFragment;
  private MenuItem menuDoneItem;

  private int maxCount = PhotoPickerFragment.DEFAULT_MAX_COUNT;

  /** to prevent multiple calls to inflate menu */
  private boolean menuIsInflated = false;

  private boolean showGif = false;
  private int columnNumber = PhotoPickerFragment.DEFAULT_COLUMN_NUMBER;
  private ArrayList<String> originalPhotos = null;


  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    boolean showCamera      = getIntent().getBooleanExtra(PhotoPickerFragment.EXTRA_SHOW_CAMERA, true);
    boolean showGif         = getIntent().getBooleanExtra(PhotoPickerFragment.EXTRA_SHOW_GIF, false);
    boolean previewEnabled  = getIntent().getBooleanExtra(PhotoPickerFragment.EXTRA_PREVIEW_ENABLED, true);

    setShowGif(showGif);

    setContentView(R.layout.picker_activity_photo_picker);

    Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(mToolbar);
    setTitle(R.string.picker_title);

    ActionBar actionBar = getSupportActionBar();

    assert actionBar != null;
    actionBar.setDisplayHomeAsUpEnabled(true);
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      actionBar.setElevation(25);
    }

    maxCount = getIntent().getIntExtra(PhotoPickerFragment.EXTRA_MAX_COUNT, PhotoPickerFragment.DEFAULT_MAX_COUNT);
    columnNumber = getIntent().getIntExtra(PhotoPickerFragment.EXTRA_GRID_COLUMN, PhotoPickerFragment.DEFAULT_COLUMN_NUMBER);
    originalPhotos = getIntent().getStringArrayListExtra(PhotoPickerFragment.EXTRA_ORIGINAL_PHOTOS);

    pickerFragment = (PhotoPickerFragment) getSupportFragmentManager().findFragmentByTag("tag");
    if (pickerFragment == null) {
      pickerFragment = PhotoPickerFragment
          .newInstance(showCamera, showGif, previewEnabled, columnNumber, maxCount, originalPhotos);
      getSupportFragmentManager()
          .beginTransaction()
          .replace(R.id.container, pickerFragment, "tag")
          .commit();
      getSupportFragmentManager().executePendingTransactions();
    }

    pickerFragment.getPhotoGridAdapter().setOnItemCheckListener(new OnItemCheckListener() {
      @Override public boolean OnItemCheck(int position, Photo photo, final boolean isCheck, int selectedItemCount) {

        int total = selectedItemCount + (isCheck ? -1 : 1);

        menuDoneItem.setEnabled(total > 0);

        if (maxCount <= 1) {
          List<String> photos = pickerFragment.getPhotoGridAdapter().getSelectedPhotos();
          if (!photos.contains(photo.getPath())) {
            photos.clear();
            pickerFragment.getPhotoGridAdapter().notifyDataSetChanged();
          }
          return true;
        }

        if (total > maxCount) {
          Toast.makeText(getActivity(), getString(R.string.picker_over_max_count_tips, maxCount),
              LENGTH_LONG).show();
          return false;
        }
        menuDoneItem.setTitle(getString(R.string.picker_done_with_count, total, maxCount));
        return true;
      }
    });

  }


  /**
   * Overriding this method allows us to run our exit animation first, then exiting
   * the activity when it complete.
   */
  @Override public void onBackPressed() {
//    if (imagePagerFragment != null && imagePagerFragment.isVisible()) {
//      imagePagerFragment.runExitAnimation(new Runnable() {
//        public void run() {
//          if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
//            getSupportFragmentManager().popBackStack();
//          }
//        }
//      });
//    } else {
//      super.onBackPressed();
//    }
    super.onBackPressed();
  }

  public void showPreviewFragment(List<String> photos, int index, int[] screenLocation, int width, int height) {
//    ImagePagerFragment imagePagerFragment =
//            ImagePagerFragment.newInstance(photos, index, screenLocation, width, height);
//    addImagePagerFragment(imagePagerFragment);
  }

//  public void addImagePagerFragment(ImagePagerFragment imagePagerFragment) {
//    this.imagePagerFragment = imagePagerFragment;
//    getSupportFragmentManager()
//        .beginTransaction()
//        .replace(R.id.container, this.imagePagerFragment)
//        .addToBackStack(null)
//        .commit();
//  }

  @Override public boolean onCreateOptionsMenu(Menu menu) {
    if (!menuIsInflated) {
      getMenuInflater().inflate(R.menu.picker_menu_picker, menu);
      menuDoneItem = menu.findItem(R.id.done);
      if (originalPhotos != null && originalPhotos.size() > 0) {
        menuDoneItem.setEnabled(true);
        menuDoneItem.setTitle(
                getString(R.string.picker_done_with_count, originalPhotos.size(), maxCount));
      } else {
        menuDoneItem.setEnabled(false);
      }
      menuIsInflated = true;
      return true;
    }
    return false;
  }


  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    if (item.getItemId() == android.R.id.home) {
      super.onBackPressed();
      return true;
    }

    if (item.getItemId() == R.id.done) {
      Intent intent = new Intent();
      ArrayList<String> selectedPhotos = pickerFragment.getPhotoGridAdapter().getSelectedPhotoPaths();
      intent.putStringArrayListExtra(PhotoPickerFragment.KEY_SELECTED_PHOTOS, selectedPhotos);
      setResult(RESULT_OK, intent);
      finish();
      return true;
    }

    return super.onOptionsItemSelected(item);
  }

  public PhotoPickerActivity getActivity() {
    return this;
  }

  public boolean isShowGif() {
    return showGif;
  }

  public void setShowGif(boolean showGif) {
    this.showGif = showGif;
  }
}

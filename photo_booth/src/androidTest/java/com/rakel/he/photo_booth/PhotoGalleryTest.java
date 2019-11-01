package com.rakel.he.photo_booth;

import androidx.test.filters.MediumTest;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.litesuits.orm.LiteOrm;
import com.rakel.he.photo_booth.contacts.PhotoGalleryContacts;
import com.rakel.he.photo_booth.model.PhotoBean;
import com.rakel.he.photo_booth.model.PhotoGalleryModel;
import com.rakel.he.photo_booth.view.PhotoGalleryView;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

//@RunWith(AndroidJUnit4.class)
@MediumTest
public class PhotoGalleryTest {
    @Rule
    public  ActivityTestRule<PhotoGalleryView> mActivityRule = new ActivityTestRule<>(
            PhotoGalleryView.class);

    protected static LiteOrm liteOrm;
    private static String TEST_PHOTO_FILE_PATH;

    private PhotoBean mTestPhotoBean;
    private PhotoGalleryModel model;

    @Before
    public void insertNewPhotoBeen()
    {

        mTestPhotoBean=new PhotoBean();
        mTestPhotoBean.setName(String.valueOf(System.currentTimeMillis()));
        mTestPhotoBean.setFilePath(TEST_PHOTO_FILE_PATH);
        liteOrm.save(mTestPhotoBean);
        model=new PhotoGalleryModel(mActivityRule.getActivity());
        model.loadPhotoes(new PhotoGalleryContacts.Model.ModelListener() {
            @Override
            public void completed(List<PhotoBean> photoes) {
                assert(photoes!=null&&photoes.size()>0);
            }
        });
    }


    /*
    for RecyclerView only visible when there's photo in the associated database.
     */
    @Test
    public void testVisibilityOfRecyclerView()
    {
        onView(withId(R.id.gallery_photoes)).check(matches(isDisplayed()));
    }


    /*
    to make sure that newly inserted item is visible and is the first item
     */
    @Test
    public void testVisibilityOfNewlyInsertedItem()
    {
        RecyclerViewMatcher withRecyclerView=new RecyclerViewMatcher(R.id.gallery_photoes);
        onView(withRecyclerView.atPosition(0))
                .check(matches(hasDescendant(withText(mTestPhotoBean.getName()))));
    }


    @After
    public void deleteNewlyInsertedPhotoBean()
    {
        liteOrm.delete(mTestPhotoBean);
    }

    @BeforeClass
    public static void  beforClassOperation()
    {
        liteOrm=LiteOrm.newSingleInstance(InstrumentationRegistry.getInstrumentation().getTargetContext(), "photo_booth.db");
        liteOrm.openOrCreateDatabase();
        copyAssetAndWrite("Espresso_Test_Photo.png");
    }


    @AfterClass
    public static void afterClassOperation()
    {
        liteOrm.close();
    }


    private static boolean copyAssetAndWrite(String fileName){
        try {
            File cacheDir;
            cacheDir = InstrumentationRegistry.getInstrumentation().getTargetContext().getCacheDir();
            if (!cacheDir.exists()){
                cacheDir.mkdirs();
            }
            File outFile =new File(cacheDir,System.currentTimeMillis()+".jpg");
            if (!outFile.exists()){
                boolean res=outFile.createNewFile();
                if (!res){
                    return false;
                }
            }else {
                if (outFile.length()>10){
                    return true;
                }
            }
            TEST_PHOTO_FILE_PATH =outFile.getAbsolutePath();
            InputStream is=InstrumentationRegistry.getInstrumentation().getTargetContext().getAssets().open(fileName);
            FileOutputStream fos = new FileOutputStream(outFile);
            byte[] buffer = new byte[1024];
            int byteCount;
            while ((byteCount = is.read(buffer)) != -1) {
                fos.write(buffer, 0, byteCount);
            }
            fos.flush();
            is.close();
            fos.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }

}

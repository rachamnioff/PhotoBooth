package com.rakel.he.photo_booth;


import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.MediumTest;
import androidx.test.rule.ActivityTestRule;

import com.litesuits.orm.LiteOrm;
import com.litesuits.orm.db.assit.WhereBuilder;
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
import org.junit.runner.RunWith;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

@RunWith(AndroidJUnit4.class)
@MediumTest
public class PhotoGalleryTest {
    @Rule
    public static ActivityTestRule<PhotoGalleryView> mActivityRule = new ActivityTestRule<>(
            PhotoGalleryView.class);

    protected static LiteOrm liteOrm;
    private static String TEST_PHOTO_NAME;

    private PhotoBean mTestPhotoBean;
    private PhotoGalleryModel model;

    @Before
    public void insertNewPhotoBeen()
    {
        mTestPhotoBean=new PhotoBean();
        mTestPhotoBean.setName(TEST_PHOTO_NAME);
        mTestPhotoBean.setFilePath(TEST_PHOTO_NAME);
        liteOrm.save(mTestPhotoBean);
        model=new PhotoGalleryModel(mActivityRule.getActivity());
        model.loadPhotoes(new PhotoGalleryContacts.Model.ModelListener() {
            @Override
            public void completed(List<PhotoBean> photoes) {
                assert(photoes!=null&&photoes.size()>0);
            }
        });
    }


    @Test
    public void testCaseForVisibilityOfRecyclerView()
    {
        onView(withId(R.id.gallery_photoes)).check(matches(isDisplayed()));

    }


    @After
    public void deleteNewlyInsertedPhotoBean()
    {
        liteOrm.delete(WhereBuilder.create(PhotoBean.class).where("filePath like ?"+new String[]{TEST_PHOTO_NAME}));
    }

    @BeforeClass
    public static void  beforClassOperation()
    {
        liteOrm=LiteOrm.newSingleInstance(mActivityRule.getActivity(), "photo_booth.db");
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
            cacheDir = mActivityRule.getActivity().getCacheDir();
            if (!cacheDir.exists()){
                cacheDir.mkdirs();
            }
            File outFile =new File(cacheDir,fileName);
            if (!outFile.exists()){
                boolean res=outFile.createNewFile();
                if (!res){
                    return false;
                }
            }else {
                if (outFile.length()>10){//表示已经写入一次
                    return true;
                }
            }
            TEST_PHOTO_NAME=outFile.getAbsolutePath();
            InputStream is=mActivityRule.getActivity().getAssets().open(fileName);
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

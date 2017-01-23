package com.nixmash.wp.migrator;

import com.nixmash.wp.migrator.db.local.model.LocalPost;
import com.nixmash.wp.migrator.db.local.service.LocalDbService;
import com.nixmash.wp.migrator.service.ImportService;
import com.nixmash.wp.migrator.service.WpApiService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kamranzafar.spring.wpapi.Post;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

import static com.nixmash.wp.migrator.config.JpaTestConfig.LOCAL;
import static org.hamcrest.Matchers.greaterThan;
import static org.junit.Assert.*;

/**
 * Created by daveburke on 1/15/17.
 */
@RunWith(SpringRunner.class)
@Transactional
public class WpApiTests extends WpSpringContext {

    @PersistenceContext(unitName = LOCAL)
    private EntityManager entityManager;

    @Autowired
    private WpApiService wpApiService;

    @Autowired
    private LocalDbService localDbService;

    @Autowired
    private ImportService importService;

    @Before
    public void clearH2Data() {
        String sql = "DELETE FROM posts;" +
                "ALTER TABLE posts ALTER COLUMN post_id RESTART WITH 1;";
        entityManager.createNativeQuery(sql).executeUpdate();
    }

    @Test
    public void getimportPostsTests() throws Exception {
        Post[] posts = wpApiService.getPosts(10);
        assertNotNull(posts);
        assertEquals(posts.length, 10);
    }

    @Test
    public void getWpApiPostsTest() throws  Exception {
        importService.importPosts();
        List<LocalPost> localPosts = localDbService.getLocalPosts();
        assertThat(localPosts.size(), greaterThan(0));
        for (LocalPost localPost : localPosts) {
            Post post = wpApiService.getPost(Math.toIntExact(localPost.getWpPostId()));
            System.out.println(post.getTitle());
//            assertEquals(localPost.getPostTitle(), post.getTitle());
        }
    }

}

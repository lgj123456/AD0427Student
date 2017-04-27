package ad0424.yls.example.com.ad0427student;

import java.util.List;

/**
 * Created by yhdj on 2017/3/4.
 */

public class AllImgListBean {

    /**
     * id : 1
     * img : http://192.168.134.79:8080/Student/img/a.jpg
     */

    private List<ImgListBean> imgList;

    public List<ImgListBean> getImgList() {
        return imgList;
    }

    public void setImgList(List<ImgListBean> imgList) {
        this.imgList = imgList;
    }

    public static class ImgListBean {
        private int id;
        private String img;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getImg() {
            return img;
        }

        public void setImg(String img) {
            this.img = img;
        }
    }
}

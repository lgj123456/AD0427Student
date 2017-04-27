package ad0424.yls.example.com.ad0427student;

import java.util.List;

/**
 * Created by yhdj on 2017/2/26.
 */

public class AllStudentListBean {
    /**
     * id : 2
     * name : a1
     * age : 16
     * sex : nan
     * headimg : http://192.168.134.79:8080/Student/img/a.jpg
     */

    private List<StuListBean> stuList;

    public List<StuListBean> getStuList() {
        return stuList;
    }

    public void setStuList(List<StuListBean> stuList) {
        this.stuList = stuList;
    }

    public static class StuListBean {
        private int id;
        private String name;
        private int age;
        private String sex;
        private String headimg;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getAge() {
            return age;
        }

        public void setAge(int age) {
            this.age = age;
        }

        public String getSex() {
            return sex;
        }

        public void setSex(String sex) {
            this.sex = sex;
        }

        public String getHeadimg() {
            return headimg;
        }

        public void setHeadimg(String headimg) {
            this.headimg = headimg;
        }
    }


    /**
     * id : 2
     * name : a1
     * age : 16
     * sex : nan
     * headimg : http://192.168.134.79:8080/Student/img/a.jpg
     */



}

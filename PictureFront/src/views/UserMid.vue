<template>
  <div id="smain-Content">
    <div class="sleft-aside">
      <h1>个人信息</h1>
      <div class="sleft-avatar">
        <a-avatar :size="ImgSize" :src="user.avatar" class="savatar-son" />
      </div>
      <div class="sleft-UserName">
        <h2>{{ user.userName }}</h2>
        <h3>编辑个人签名</h3>
      </div>
      <el-divider></el-divider>
      <div class="sleft-iteam">
        <span class="span0"></span>
        <i :class="user.sex == '男' ? 'el-icon-male' : 'el-icon-female'"></i>:
        <span>{{ user.sex }}</span>
      </div>
      <div class="sleft-iteam">
        <span class="span0"></span>
        <i class="el-icon-location-information"></i>:
        <span>{{ user.city }}</span>
      </div>
      <div class="sleft-iteam">
        <span class="span0"></span>
        <i class="el-icon-message"></i>:
        <span>{{ user.email }}</span>
      </div>
      <div class="sleft-iteam">
        <span class="span0"></span>
        <i class="el-icon-mobile-phone"></i>:
        <span>{{ user.phone }}</span>
      </div>
    </div>

    <div class="smid-content">


      <h1>存储信息</h1>
      <div class="smid-content-1">
        <a-statistic title="总照片数：" :value="imgInfo.imageSumNumber" suffix="张" />
        <a-statistic title="现占用内存:" :precision="2" :value="imgInfo.imageSumSize" suffix="Mb" />
      </div>
    </div>

  </div>

</template>

<script>
export default {
  name: 'UserMid',
  data() {
    return {

      dialogVisible: false,
      display: true,
      toekn: '',
      user: {
        avatar: "/static/avatar/default.jpg",
        birthday: "2022-08-20 00:00:00",
        capacity: 1000,
        city: "黑龙江; 哈尔滨",
        email: "1609048001@qq.com",
        passWord: "123456",
        sex: '',
        phone: "13891531948",
        userId: 1,
        userName: "User"
      },
      ImgSize: 12,

      serveUrL: this.$serveUrL,
      Isrouter: true,
      imageUrl: '/default.jpg',
      /* */
      //用户内存
      imgInfo: {
        capacity: 10025,//图片总内存
        imageSumSize: 150, //所用内存
        imageSumNumber: 65,//图片总数
      },
      percentage: 11.5,   //比例

    };
  },
  computed: {

  },
  props: ['userprop'],
  // 监听父组件的值
  watch: {
    userprop(n, o) {
      this.user = n;
      o;
    }
  },
  created() {

  },
  mounted() {
    var cookie = this.$cookie.getCookie();
    if (cookie[0] != "token") {
      this.$message({
        message: '登录已过期!',
        type: 'error'
      });
      this.$router.push({ name: 'login' });
    }
    else {
      this.token = cookie[1];
    }
    this.user = this.userprop;
    this.ImgSize = window.innerWidth / 12;
    // 监听视口宽度，改变头像大小
    window.onresize = () => {
      return (() => {
        this.ImgSize = window.innerWidth / 12;
      })();
    }
    this.selcetInfo();
  },
  methods: {
    selcetInfo() {
      var _this = this;
      const formData = new FormData();
      formData.append('token', this.token);
      this.axios({
        url: this.serveUrL + "/visual/selectImageInfo",
        method: "post",
        data: formData,
      }).then(function (resp) {
        if (resp.data.status == 'success') {
          _this.imgInfo = resp.data.data;
        }
      }
      );

    },
    handleAvatarSuccess(res, file) {
      this.imageUrl = URL.createObjectURL(file.raw);
    },
    beforeAvatarUpload(file) {
      const isJPG = file.type === 'image/jpeg';
      const isLt2M = file.size / 1024 / 1024 < 2;

      if (!isJPG) {
        this.$message.error('上传头像图片只能是 JPG 格式!');
      }
      if (!isLt2M) {
        this.$message.error('上传头像图片大小不能超过 2MB!');
      }
      return isJPG && isLt2M;
    }
  }
}
</script>
<style >
#smain-Content {

  border: #d9d9d9 solid 1px;
  margin: 2% 4% 1% 4%;
  width: 90%;
  height: 90%;
  background-color: rgb(255, 255, 255);
}


.sleft-aside {
  border-right: #ebe6cf solid 1px;
  margin-left: 2%;
  width: 40%;
  height: 100%;
  float: left;
}

.sleft-aside h1 {
  font-size: 20px;
}

.sleft-avatar {
  width: 100%;
  height: 30%;
  text-align: center;
}

.savatar-son {
  margin-top: 8%;
}

.sleft-UserName {
  width: 100%;
  height: 10%;
}

.sleft-UserName h2 {
  font-size: 22px;
  text-align: center;
}

.sleft-UserName h3 {
  text-align: center;
}

.sleft-iteam {

  margin: 6% 0;
  font-size: 20px;
  margin-left: 10%;
}

.sleft-iteam span {
  margin-left: 1px;
}

.span0 {
  color: black;
}



.smid-content {
  /* border: #d9d9d9 solid 1px;
   */
  margin-left: 2%;
  width: 55%;
  height: 100%;
  float: left;
}

h1 {
  font-size: 20px;
}

.smid-content-1 {
  margin: 10%;
  float: left;
}

.smid-content-2 {
  position: relative;
  float: right;
  margin-right: 20%;
  margin-top: 10%;
}
</style>
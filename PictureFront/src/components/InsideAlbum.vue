<template>
  <div id="main">
    <!-- 标题 -->
    <div :style="displayTopMenu" id="album_top">
      <a class="album-icon" style="left:20px" title="返回首页" @click="$router.push({ name: 'Album' });">
        <svg-icon icon-file-name="home" />
      </a>

      <a class="album-icon" style="right:30px" title="分享相册" @click="shareAlbum()">
        <svg-icon icon-file-name="" />
      </a>
      <span>{{ currentAlbumName }}</span>
    </div>
    <!-- 选中菜单 -->
    <div id="albumCheck_menu" :style="displayCheckedMenu">
      <div class="check_menu_item">
        <div> </div>
        <div class="check_menu_txt"><a style="vertical-align:middle;margin-right: 1.5em;"
            @click="handleCheckAllChange(false)">
            <svg-icon icon-file-name="chacha" />
          </a>已选择{{ checkedCount }}张图片</div>
      </div>
      <div class="check_menu_feature">
        <a title="全选图片" @click="handleCheckAllChange(!checkedStatus)">
          <svg-icon icon-file-name="all">
          </svg-icon>
        </a>
        <a title="移除相册" @click="removeImage()">
          <svg-icon icon-file-name="remove" />
        </a>
        <a title="下载图片" @click="downloadImage()">
          <svg-icon icon-file-name="download" />
        </a>
        <a title="修改封面" @click="setAlbumCover()">
          <svg-icon icon-file-name="set" />
        </a>

      </div>
    </div>
    <el-divider></el-divider>
    <!-- 图片区域 -->

    <el-dialog title="相册分享" :visible.sync="shareDialogVisible" width="300px" style="text-align: center;">
     
      <el-radio-group v-model="shareRadio" @change="changeRadio()">
        
        <el-radio :label="7">7天</el-radio>
        <el-radio :label="30">30天</el-radio>
        <el-radio :label="0">永久</el-radio>
      </el-radio-group>
      <el-button type="text" @click="copyUrL()">复制链接</el-button>
      <div id="qrcode" ref="qrcode" style="overflow:hidden;margin-left:65px;text-align: center;">
        
      </div>
      <p style="margin:2px">手机扫码查看/<a @click="saveQRCode()">保存二维码</a></p>
<!--  图片显示，增加弹窗 -->
    </el-dialog>
    <div id="album-content" :style="empty1">
      <div id="album-image-content">
        <div v-for="(group, i) in images" :key="i" class="album-image-group">

          <!-- 时间组标题和复选框 -->
          <div class="album-image-time">
            <el-checkbox
                :style="displayCheckedMenu"
                class="time-select"
                @change="handleCheckPartChange(i, checkedPart)">
            </el-checkbox>
            <span class="time-txt"> {{ group.time }}</span>
          </div>

          <!-- 图片列表 -->
          <div class="image-item" v-for="(item, j) in group.image" :key="j">

            <!-- 单张图片复选框 -->
            <el-checkbox-group v-model="checkedImgIndex" @change="handleCheckedImgChange" class="check-box"
                               :style="displayCheckedMenu">
              <el-checkbox :label="i + ',' + j"></el-checkbox>
            </el-checkbox-group>

            <!-- 图片及按钮容器 -->
            <div class="image-wrapper" style="position: relative; display: inline-block;">

              <!-- 图片组件 -->
              <el-image
                  :src="serveUrL + item.compressUrL"
                  :preview-src-list="getSrcList(i, j)"
                  class="image"
                  lazy
                  style="cursor: pointer;"
              >
                <div slot="placeholder" class="image-slot" style="margin-top:3em">
                  <svg-icon icon-file-name="loading" style="vertical-align:-5px"></svg-icon>加载中
                </div>
              </el-image>

              <!-- 查看信息按钮，使用绝对定位覆盖在图片右上角 -->
              <el-button
                  icon="el-icon-info"
                  circle
                  size="mini"
                  class="info-btn"
                  @click.stop="openDialog(item)"
                  title="查看信息"
                  style="position: absolute; top: 5px; right: 5px; z-index: 10;"
              ></el-button>

            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 弹窗 -->
    <el-dialog :visible.sync="showDialog" width="400px" :before-close="closeDialog">
      <div v-if="selectedImage">
        <img :src="serveUrL + selectedImage.compressUrL" alt="图片" style="width: 100%; margin-bottom: 10px;" />
        <p><strong>拍摄地址：</strong> {{ selectedImage.imageSite }}</p>
        <p><strong>图片注释：</strong> {{ selectedImage.imageDesc }}</p>
      </div>
    </el-dialog>

    <div :style="empty2">
      <el-empty :image-size="200" description="暂无图片">
        <el-button type="primary" @click="$router.push({ name: 'upload' });">去上传</el-button>
      </el-empty>
    </div>


  </div>
</template>

<script>
import QRCode from 'qrcodejs2'
export default {
  name: 'InsideAlbum',
  data() {
    return {
      showDialog: false,          // 控制弹窗显示
      selectedImage: null,        // 当前被点击的图片信息
      // 相册分享有效期天数
      shareAlbumUrL:'http://xiaohua.live',
      shareRadio:7,
      shareDialogVisible: false,
      currentAlbumId: 0,
      currentAlbumName: '',
      token: '',  //token凭证
      isRouterAlive: true,
      serveUrL: this.$serveUrL,
      // 后端查询的图片数据
      images: [
        {
          time: '2021年8月31日星期一',
          image: [
            {
              imageId: '',
              compressUrL: 'https://cube.elemecdn.com/6/94/4d3ea53c084bad6931a56d5158a48jpeg.jpeg',
              imageName: '',
            },
            {
              imageId: '',
              compressUrL: 'https://cube.elemecdn.com/6/94/4d3ea53c084bad6931a56d5158a48jpeg.jpeg',
              imageName: '',
            }
          ]
        }
      ],
      previewImageUrL: [
        '',
      ],
     

      //复选框
      checkAll: false,       //是否全选
      checkedImgIndex: [],   //图片索引数组
      checkedImgId: [],      //图片id数组
      isIndeterminate: false,

      //用户勾选图片，菜单的显示与隐藏
      checkedStatus: false,//是否全选
      checkedPart: true,//是否 部分选
      checkedCount: 0, //勾选数量
      displayTopMenu: 'true', //显示
      displayCheckedMenu: '',  //隐藏

      empty1: 'display:block',
      empty2: 'display:none',


      // 相册信息修改
      dialogFormVisible: false,
      form: {
        name: '',
        title: '',
        content: '',
        delivery: false,
        type: [],
        resource: '',
        desc: ''
      },
      formLabelWidth: '100px'
    };
  },
  created() {

  },

  // 不是this.roueter!!!!!!!!!!!!!
  mounted() {

    this.currentAlbumId = this.$route.query.albumId;
    this.currentAlbumName = this.$route.query.albumName;
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
    this.selectAllImage();


  },
  methods: {
    openDialog(image) {
      console.log("clicked!");
      this.selectedImage = image;
      this.showDialog = true;
    },
    closeDialog(done) {
      this.showDialog = false;
      this.selectedImage = null;
      done();
    },
    selectAllImage() {
      var _this = this;
      const formData = new FormData();
      formData.append('token', this.token);
      formData.append('albumId', this.currentAlbumId);
      this.axios({
        url: this.$serveUrL + '/album/selectAllImage',
        method: "post",
        data: formData
      }).then(function (resp) {
        if (resp.data.status == "success") {
          _this.images = resp.data.data.images;
          _this.previewImageUrL = resp.data.data.previewImageUrL;
          for (var i = 0; i < _this.previewImageUrL.length; i++) {
            _this.previewImageUrL[i] = _this.serveUrL + _this.previewImageUrL[i]
          }
          _this.isRouterAlive = !_this.isRouterAlive;
          //判断当前页面数据是否为空
          if (_this.images.length < 1) {
            _this.empty1 = 'display:none';
            _this.empty2 = 'display:block';
          }
          else {
            _this.empty1 = 'display:block';
            _this.empty2 = 'display:none';
          }
        }
      })
    },
    // 获取服务端分享相册链接
    getShareAlbumUrL(){
      let _this = this;
      const formData = new FormData();
      formData.append('token', this.token);
      formData.append('albumId',this.currentAlbumId);
      formData.append('shareDay', this.shareRadio);
      this.axios({
        url: this.$serveUrL + "/album/share",
        method: "post",
        data: formData
      }).then(function (resp) {
        let cur = document.location.href;
        let pathName =  _this.$route.path;
        console.log(pathName)
         let index =  cur.indexOf(pathName);
        // 拼接分享链接
        let url = cur.substring(0,index)+'/shareAlbum'+resp.data.UrL;
        _this.shareAlbumUrL = url;
        console.log(url)
        _this.createQRCode();
      })
    },
    // 分享相册
    // shareAlbum() {
    //   this.shareDialogVisible = true;
    //   this.changeRadio();
    // },
    // 改变时间期限
    changeRadio(){
       this.getShareAlbumUrL();
      // 重新生成二维码   
    },
    // 复制链接
    copyUrL() {
      let oInput = document.createElement('input');
      // 将想要复制的值
      oInput.value = this.shareAlbumUrL;
      // // 页面底部追加输入框
      document.body.appendChild(oInput);
      // // 选中输入框
      oInput.select();
      // 执行浏览器复制命令
      document.execCommand('Copy');
      oInput.remove();
      this.$message({
        message: '相册分享链接复制成功',
        type: 'success'
      });
    },
    //生成二维码
    createQRCode(){
      this.$nextTick(function () {
        let dom = document.getElementById("qrcode");
        // 删除上一个节点
        dom.innerHTML = '';
        new QRCode(this.$refs.qrcode, {
          text: this.shareAlbumUrL,//url地址  文本等需要转换为二维码的内容
          width: 130,
          height: 130,
          colorDark: "#333333", //二维码颜色
          colorLight: "#ffffff", //二维码背景色
        });
        if(this.shareRadio == 0){
          this.$message({
        message: '有效期为永久',
        type: 'success'
      });
        }
        else
        this.$message({
        message: '有效期为'+this.shareRadio+'天',
        type: 'success'
      });
      });
    },
    // 保存二维码
    saveQRCode() {
      const link = document.createElement('a');
      link.download = '《'+this.currentAlbumName+'》相册二维码.png';
      link.style.display = 'none';
      let imgURL = document.getElementById('qrcode').getElementsByTagName('canvas');
   
      link.href = imgURL[0].toDataURL('image/jpg');
      document.body.appendChild(link);
      link.click();
      URL.revokeObjectURL(link.href); //释放URL对象
      document.body.removeChild(link);
    },
    setAlbumCover() {
      this.handleCheckIndexToId();
   
      if (this.checkedImgIndex.length > 1) {

        this.$message({
          message: '您只能选择一张图片作为相册封面',
          type: 'warning'
        });
        return;
      }
      this.$confirm('是否将该图片设置为相册封面?', '设置相册封面', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {

        var _this = this;
        console.log(this.checkedImgId);
        const formData = new FormData();
        formData.append('token', this.token);
        formData.append('albumId', this.currentAlbumId);
        formData.append('imageId', this.checkedImgId[0]);
        this.axios({
          url: this.$serveUrL + "/album/setAlbumCover",
          method: "post",
          data: formData
        }).then(function (resp) {
          if (resp.data.status == "success") {
            _this.$message({
              message: '相册封面设置成功！',
              type: 'success'
            });
            _this.handleCheckAllChange(false);
            _this.selectAllImage();
          }
          else {
            1;
          }
        })
      })
    },
    removeImage() {
      this.$confirm('此操作将移出相册集, 是否继续?', '移除相册', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.handleCheckIndexToId();
        var _this = this;
        console.log(this.checkedImgId);
        const formData = new FormData();
        formData.append('token', this.token);
        formData.append('albumId', this.currentAlbumId);
        formData.append('imageId', this.checkedImgId);
        this.axios({
          url: this.$serveUrL + "/album/removeImageFromAlbum",
          method: "post",
          data: formData
        }).then(function (resp) {
          if (resp.data.status == "success") {
            _this.$message({
              message: '图片移除成功！',
              type: 'success'
            });
            _this.handleCheckAllChange(false);
            _this.selectAllImage();
          }
          else {
            1;
          }
        })
      })
    },
    downloadImage() {
      var imgIndex = this.checkedImgIndex;
      var str = imgIndex[0].split(',');
      var count = 0
      for (let i = 0; i < str[0]; i++) {
        for (let j = 0; j < this.images[i].image.length; j++) {
          count++;
        }
      }
      count += parseInt(str[1]);//在previewImageUrL的位置,根据选中index排序完的顺序下载
      for (let i = 0; i < imgIndex.length; i++) {
        str = imgIndex[i].split(',');
        var imgUrl = this.previewImageUrL[count + i];
        var imgName = this.images[str[0]].image[str[1]].imageName;
        this.downloadPicture(imgUrl, imgName);
      }
      this.handleCheckIndexToId();
      var _this = this;
      const formData = new FormData();
      formData.append('token', this.token);
      formData.append('number', this.checkedImgId.length);
      this.axios({
        url: this.$serveUrL + "/record/recordDownload",
        method: "post",
        data: formData
      }).then(function (resp) {
        if (resp.data.status == "success") {
          _this.$message({
            message: '图片下载成功！',
            type: 'success'
          });
          _this.dialogTableVisible = false;
        }
        else {
          1;
        }
      })
    },
    downloadPicture(imgSrc, name) {
      // 根据url下载
      fetch(imgSrc).then((res) => {
        res.blob().then((blob) => {
          const url = window.URL.createObjectURL(blob)
          const a = document.createElement('a')
          a.href = url
          a.download = name
          a.click()
          window.URL.revokeObjectURL(url)
        })
      })
    },
    // 关闭


    // 其中大图预览目前总是从第一张开始,简单处理下,就是构建一个以当前图片为起始的数组
    getSrcList(x, y) {
      var count = 0;
      for (var i = 0; i < x; i++) {
        for (var j = 0; j < this.images[i].image.length; j++) {
          count++;
        }
      }

      return this.previewImageUrL.slice(count + y).concat(this.previewImageUrL.slice(0, count + y));
    },
    // 显示或隐藏顶部菜单和选中菜单
    SwitchDisplay(checkedCount) {
      if (checkedCount > 0) {
        // 获取顶部菜单高度，使选中菜单保持一致
        if (this.displayTopMenu != "display:none;" || this.displayTopMenu == '') {
          this.displayCheckedMenu = 'display:block;'
          this.displayTopMenu = 'display:none;'
        }
      }
      else {
        this.displayCheckedMenu = '';
        this.displayTopMenu = '';
      }
    },
    //部分选
    handleCheckPartChange(x, status) {
      
      if (status) {
        let len = this.images[x].image.length;
       
        this.checkedCount = len;
        this.checkedImgIndex = new Array(len);
        for (var i = 0; i < len; i++) {
          this.checkedImgIndex[i] = x + ',' + i;
        }
      }
      else {
        this.checkedImgIndex = [];
        this.checkedCount = 0;
      }
      this.isIndeterminate = false;
      this.checkedPart = !this.checkedPart;
      this.SwitchDisplay(this.checkedCount);

    },

    //全选框
    handleCheckAllChange(val) {
      if (val) {
        let len = this.images.length;
        var count = 0;
        for (var i = 0; i < len; i++) {
          for (var j = 0; j < this.images[i].image.length; j++) {
            count++;
          }
        }
        this.checkedCount = count;

        this.checkedImgIndex = new Array(count);
        count = 0;
        for (i = 0; i < len; i++) {
          for (j = 0; j < this.images[i].image.length; j++) {
            this.checkedImgIndex[count++] = i + ',' + j;
          }
        }
      }
      else {
        this.checkedImgIndex = [];
        this.checkedCount = 0;
      }
      this.isIndeterminate = false;
      this.checkedStatus = val;
      this.SwitchDisplay(this.checkedCount);
    },
    //根据选中的索引转化为图片的id
    handleCheckIndexToId() {
      let len = this.checkedImgIndex.length;
      let array = [];
      this.checkedImgId = new Array(len);
      for (let i = 0; i < len; i++) {
        let str = this.checkedImgIndex[i].split(',');
        array.push(this.images[str[0]].image[str[1]].imageId);
       

      }
      this.checkedImgId = array;
    },
    // 部分被选中,当勾选时，上方显示操作菜单
    handleCheckedImgChange(value) {
      this.checkedImgIndex.sort();

      this.checkedCount = value.length;
      this.SwitchDisplay(this.checkedCount);
      this.checkAll = this.checkedCount === this.images.length;
      this.isIndeterminate = this.checkedCount > 0 && this.checkedCount < this.images.length;
    },



  }

}


</script>

<style scoped>
/* 隐藏菜单 */
#albumCheck_menu {
  background-color: #3174ff;
  height: 9%;
  width: 100%;
  display: none;
  position: relative;
}

.check_menu_txt {
  font-family: MiSans, MIUI, Helvetica Neue, Helvetica, Arial, PingFang SC, Microsoft Yahei, Hiragino Sans GB, Heiti SC, WenQuanYi Micro Hei, sans-serif;
  color: #fff;
  font-size: 1.35em;
}

.check_menu_item {
  display: inline-block;
  position: absolute;
  top: 50%;
  left: 5%;
  transform: translate(-5%, -50%);
}

.check_menu_feature {
  right: 5%;
  position: absolute;
  top: 50%;
  transform: translate(-5%, -50%);
}



/* css */
#main {
  height: 100%;
  width: 100%;
  overflow-x: hidden;
}

#album_top {
  height: 9%;
  width: 100%;
  position: relative;
  text-align: center;
}







/*--图片区域--*/
#album-content {
  width: 95%;
  margin-left: 5%;
  margin-top: 1%;
  height: 88%;
  overflow: scroll;
  overflow-x: hidden;
}

#album-image-content {
  width: 100%;
  height: 100%;

}

.album-image-group {
  margin-top: 15px;
}

.album-image-time {
  height: 45px;
  position: relative;
  font-size: 1.5em;

  display: block;
}

/* .album-image-time:hover+.check-box {
  display: block;
} */

.time-select {
  display: none;
  position: absolute;
  top: 5px;
}

.time-txt {
  position: absolute;
  top: 5px;
  left: 25px;
  display: block;
}


.image-item {
  margin-right: 10px;
  position: relative;
  display: flex;
  display: inline-block;
}

@media screen and (max-width: 960px){
  /* 手机端CSS代码 */
  .image {
    height: 11em;
    max-width: 8em;
    
  }
  .album-icon{
  position: absolute;
  top: 20%;
  width: 5%;
  height: 80%;
  font-size: 1.5em;
 
}

#album_top span {
  position: absolute;
  top: 22%;
  text-align: center;
  line-height: 1.5;
  font-size: 1.5em;
  color: rgba(34, 28, 28, 0.65);
  font-family: MiSans, serif;
  overflow: hidden;
}

.check_menu_feature a {
  margin-right: 0.5em;
  font-size: 1.35em;
}
}

@media screen and (min-width: 960px){
  /* 电脑端CSS代码 */
  .image {
    height: 13em;
    
  }
  .album-icon{
  position: absolute;
  top: 20%;
  width: 5%;
  height: 80%;
  font-size: 2em;
 
}
#album_top span {
  position: absolute;
  top: 22%;
  text-align: center;
  line-height: 1.5;
  font-size: 2em;
  color: rgba(34, 28, 28, 0.65);
  font-family: MiSans, serif;
  overflow: hidden;
}
.check_menu_feature a {
  margin-right: 2em;
  font-size: 1.35em;
}
}


.check-box {
  width: 100%;
  margin: 5px;
  display: none;
  position: absolute;
}

.image-item:hover .check-box {
  display: block;
}

.image-wrapper {
  position: relative;
  width: 100%;
  height: 100%;
}

.info-btn {
  position: absolute;
  top: 6px;
  right: 6px;
  z-index: 10;
  background-color: rgba(255, 255, 255, 0.8);
  border: none;
}

</style>


<style>
#app {
  height: 100%;
  width: 100%;
}

.el-checkbox__inner {
  background-color: white;
  opacity: 0.9;
  border: #3174ff 1px solid;
  border-radius: 100px;
  width: 1.4em;
  height: 1.4em;
}

.el-checkbox__inner::after {
  height: 0.68em;
  left: 0.42em;
  width: 0.29em;
}

/* 增大点击区域，提高用户体验 */
.el-checkbox__inner::before {
  content: '';
  position: absolute;
  top: -0.625em;
  right: -2.5em;
  bottom: -1.875em;
  left: -0.75em;
}


.el-divider {
  margin: 0;
}
</style>
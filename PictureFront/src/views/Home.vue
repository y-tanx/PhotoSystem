<template>
  <div id="main">
    <!-- 顶部菜单 ref:组件对象，获取高度 horizontal:横向导航栏-->
    <el-menu :default-active="activeIndex" ref="top_menu" mode="horizontal" @select="handleSelect"
      background-color="#545c64" text-color="#fff" active-text-color="#ffd04b" :style="displayTopMenu">
      <el-menu-item index="all">全 部 图 片</el-menu-item>
      <el-submenu index="type" style="width:130px;">
        <template slot="title">{{ typeTitle }}</template>
        <el-menu-item v-for="(item, index) in imageTypes " :key="index" :index="item">{{ item }}</el-menu-item>
      </el-submenu>
      <el-submenu index="time" style="width:130px">
        <template slot="title">{{ timeTitle }}</template>
        <el-menu-item v-for="(item, index) in imageTimes" :key="index" :index="item">{{ item }}</el-menu-item>
      </el-submenu>
    </el-menu>

    <!-- 选中菜单 -->
    <div id="check_menu" :style="displayCheckedMenu">
      <div class="check_menu_item"> <a @click="handleCheckAllChange(false)">
          <svg-icon icon-file-name="chacha" />
        </a></div>
      <div class="check_menu_item check_menu_txt">已选择{{ checkedCount }}张图片</div>
      <div class="check_menu_feature">
        <a title="全选图片" @click="handleCheckAllChange(!checkedStatus)">
          <svg-icon icon-file-name="all">
          </svg-icon>
        </a>
        <a title="添加相册" @click="openAlbum()">
          <svg-icon icon-file-name="add" />
        </a>
        <a title="下载图片" @click="downloadImage()">
          <svg-icon icon-file-name="download" />
        </a>
        <a title="删除图片" @click="deleteImage()">
          <svg-icon icon-file-name="delete" />
        </a>
      </div>
    </div>

    <!-- 添加相册表格 -->
    <el-dialog title="添加相册" :visible.sync="dialogTableVisible" width="25%">
      <el-table ref="singleTable" :data="albumData" :show-header="false" highlight-current-row max-height="300px"
        @current-change="handleTableCurrentChange">
        <el-table-column property="albumImg" label="描述图片" width="180">
          <!-- 将图片添加到表格中 -->
          <template slot-scope="scope">
            <img :src="scope.row.albumImg" style="height:5em;width:9em" />
          </template>
        </el-table-column>
        <el-table-column property="albumName" label="相册名称">
        </el-table-column>
      </el-table>
      <el-button type="primary" size="small" style="margin-left: 45%; margin-top: 10px;" @click="addAlbum()">添加
      </el-button>
    </el-dialog>

    <!-- 图片及复选框 -->
    <div id="image-content">
      <div class="image-item" v-for="(item, index) in images" :key="index" :style="reactiveImage">
        <!-- 复选框 -->
        <el-checkbox-group
            v-model="checkedImgIndex"
            @change="handleCheckedImgChange"
            class="check-box"
            :style="displayCheckedMenu"
        >
          <el-checkbox :label="index"></el-checkbox>
        </el-checkbox-group>

        <!-- 包裹 el-image 和按钮 -->
        <div class="image-wrapper">
          <!-- el-image 预览不变 -->
          <el-image
              :src="item.compressUrL"
              :preview-src-list="getSrcList(index)"
              class="image"
              fit="cover"
          >
            <div
                slot="placeholder"
                class="image-slot"
                element-loading-text="图片加载中..."
                v-loading="true"
                style="margin-top:40%"
            ></div>
          </el-image>

          <!-- 弹窗按钮 -->
          <el-button
              icon="el-icon-info"
              circle
              size="mini"
              class="info-btn"
              @click.stop="openDialog(item)"
              title="查看信息"
          ></el-button>
        </div>
      </div>
    </div>

    <el-dialog
        :visible.sync="showDialog"
        width="400px"
        :before-close="closeDialog"
        title="图片详情"
    >
      <div v-if="selectedImage">
        <img
            :src="selectedImage.compressUrL"
            alt="图片"
            style="width: 100%; margin-bottom: 10px;"
        />

        <!-- 拍摄地址：改为可编辑 -->
        <el-form label-width="80px" size="small">
          <el-form-item label="拍摄地址">
            <el-input v-model="selectedImage.imageSite" placeholder="请输入拍摄地址" />
          </el-form-item>

          <el-form-item label="图片注释">
            <el-input
                type="textarea"
                v-model="selectedImage.imageDesc"
                placeholder="请输入图片注释"
            />
          </el-form-item>
        </el-form>

        <!-- 按钮区域 -->
        <div style="text-align: right; margin-top: 10px;">
          <el-button size="small" @click="closeDialog">取 消</el-button>
          <el-button type="primary" size="small" @click="saveImageInfo">保存修改</el-button>
        </div>
      </div>
    </el-dialog>

    <!-- 分页条 -->
    <div id="pagination">
      <div id="pagination-son">
        <el-pagination @size-change="handleSizeChange" @current-change="handleCurrentChange" :current-page="currentPage"
          :page-sizes="page_size" :page-size="pageSize" layout="total, sizes, prev, pager, next, jumper"
          :total="totalCount">
        </el-pagination>
      </div>
    </div>

  </div>
</template>

<script>
export default {
  data() {
    return {
      
      states: ["风景", "动物"],
      loading: false,
      token: '',  //token凭证
      serveUrL: this.$serveUrL,
      // 顶部菜单
      activeIndex: 'all',
      timeTitle: '拍 摄 时 间',
      typeTitle: '图 片 类 别',
      arrPath: ['all'],   //存放当前菜单路径
      isRouterAlive: true,

      // 后端查询的图片数据
      images: [
        {
          imageId: '',
          compressUrL: 'https://cube.elemecdn.com/6/94/4d3ea53c084bad6931a56d5158a48jpeg.jpeg',
          imageName: '',
        }
      ],
      previewImageUrL: [
        'https://cube.elemecdn.com/6/94/4d3ea53c084bad6931a56d5158a48jpeg.jpeg',
      ],

      //添加到相册
      dialogTableVisible: false,//菜单状态
      currentRowData: null, //当前行相册数据对象
      //相册数据
      albumData: [
        {
          albumName: '',
          albumImg: '',
          albumId: '',
        }
      ],

      // 响应式布局图片大小css
      reactiveImage: {
        margin: '0.3em',
        width: '9%',
        height: '29%',
      },
      imageTypes: ['美女', '宠物', '植物'],//所有图片类型
      imageTimes: ['2021-8-26', '2022-9-23', '2022-9-26'],  //所有图片时间

      //复选框
      checkAll: false,       //是否全选
      checkedImgIndex: [],   //图片索引数组
      checkedImgId: [],      //图片id数组
      isIndeterminate: false,

      //用户勾选图片，菜单的显示与隐藏
      checkedStatus: false,
      checkedCount: 0, //勾选数量
      displayTopMenu: 'true', //显示
      displayCheckedMenu: '',  //隐藏

      //  分页模块
      page_size: [10, 15, 20, 25, 30, 40],
      pageSize: 20,// 每页显示的条数
      totalCount: 100, // 总记录数
      currentPage: 1, // 当前页码

      showDialog: false,       // 控制弹窗显示与隐藏
      selectedImage: null,     // 当前点击的图片对象（用于在弹窗中展示详情）

    };
  },
  created() {

  },
  beforeCreate() {
  },
  mounted() {

    var cookie = this.$cookie.getCookie();
    console.log(cookie)
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
    this.selectAllTimeType();
    this.selectAllImage();
  },
  methods: {
    openDialog(item) {
      this.selectedImage = { ...item };  // 复制一份避免直接影响原数据
      this.showDialog = true;
    },
    closeDialog() {
      this.showDialog = false;
      this.selectedImage = null;
    },
    saveImageInfo() {
      var _this = this;
      const formData = new FormData();
      formData.append("token", this.token);
      formData.append("imageId", this.selectedImage.imageId);
      formData.append("imageSite", this.selectedImage.imageSite);
      formData.append("imageDesc", this.selectedImage.imageDesc);

      this.axios({
        url: this.$serveUrL + "/image/update",
        method: "post",
        data: formData
      }).then(function (resp) {
        if (resp.data.status === "success") {
          _this.$message({
            message: '图片信息更新成功！',
            type: 'success'
          });
          _this.showDialog = false;
          _this.loadImages(); // 重新加载图片
        } else {
          _this.$message({
            message: '图片信息保存失败！',
            type: 'error'
          });
        }
      }).catch(function (error) {
        console.error("保存出错", error);
        _this.$message({
          message: '请求出错',
          type: 'error'
        });
      });
    },

    // 添加到相册表格方法
    handleTableCurrentChange(val) {
      this.currentRowData = val;
    },
    // 其中大图预览目前总是从第一张开始,简单处理下,就是构建一个以当前图片为起始的数组
    getSrcList(index) {
      return this.previewImageUrL.slice(index).concat(this.previewImageUrL.slice(0, index))
    },
    openAlbum() {
      this.selectAlbums();
      this.dialogTableVisible = true;
    },
    loadImages() {
      if (this.arrPath && this.arrPath.length > 0) {
        this.handleSelect('', this.arrPath);
      } else {
        // 如果路径为空，默认加载全部图片
        this.selectAllImage('', '');
      }
    },
    addAlbum() {
      this.handleCheckIndexToId();
      var _this = this;
      const formData = new FormData();
      formData.append('token', this.token);
      formData.append('imageId', this.checkedImgId);
      formData.append('albumId', this.currentRowData.albumId);
      this.axios({
        url: this.$serveUrL + "/album/addImageToAlbum",
        method: "post",
        data: formData
      }).then(function (resp) {
        if (resp.data.status == "success") {
          _this.$message({
            message: '添加相册成功！',
            type: 'success'
          });
          _this.dialogTableVisible = false;
        }
        else {
          1;
        }
      })
    },
    async downloadImage() {
      const imgIndex = this.checkedImgIndex;

      for (let i = 0; i < imgIndex.length; i++) {
        const idx = imgIndex[i];
        const imgUrl = this.previewImageUrL[idx];
        const imgName = this.images[idx].imageName;
        await this.downloadPicture(imgUrl, imgName);
        // 延时100ms，保证浏览器能触发下载
        await new Promise(resolve => setTimeout(resolve, 200));
      }

      this.handleCheckIndexToId();

      this.$message({
        message: '图片下载成功！',
        type: 'success'
      });
      this.dialogTableVisible = false;
    },

    downloadPicture(imgSrc, name) {
      return new Promise((resolve) => {
        const a = document.createElement('a');
        a.href = imgSrc;
        a.download = name;

        // 触发点击下载
        document.body.appendChild(a);
        a.click();
        document.body.removeChild(a);

        resolve();
      });
    },
    deleteImage() {
      this.$confirm('此操作将删除所选图片, 是否继续?', '删除图片', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.handleCheckIndexToId();
        var _this = this;
        const formData = new FormData();
        formData.append('token', this.token);
        formData.append('imageId', this.checkedImgId);
        this.axios({
          url: this.$serveUrL + "/image/delete",
          method: "post",
          data: formData
        }).then(function (resp) {
          if (resp.data.status == "success") {
            _this.$message({
              message: '图片删除成功！',
              type: 'success'
            });
            _this.handleCheckAllChange(false);
            _this.handleSelect('', _this.arrPath);
          }
          else {
            1;
          }
        })
      })
    },
    selectAlbums() {
      var _this = this;
      const formData = new FormData();
      formData.append('token', this.token);
      this.axios({
        url: this.$serveUrL + "/album/selectAlbumName",
        method: "post",
        data: formData
      }).then(function (resp) {
        if (resp.data.status == "success") {

          _this.albumData = resp.data.data;
        }
        else {
          1;
        }
      })
    },
    selectAllTimeType() {
      var _this = this;
      const formData = new FormData();
      let requestApi = '/image/selectTimeType';
      formData.append("token", this.token);
      this.axios({
        url: this.$serveUrL + requestApi,
        method: "post",
        data: formData
      }).then(function (resp) {

        if (resp.data.status == "success") {
          let data = resp.data.data;
          _this.imageTypes = data.imageType;
          _this.imageTimes = data.imageDate;
          _this.isRouterAlive = !_this.isRouterAlive;

        }
      })
    },
    handleSelect(value, path) {
      this.arrPath = path;
      value
      switch (path[0]) {
        case "all": {
          this.selectAllImage('', '');
          this.timeTitle = '拍 摄 时 间';
          this.typeTitle = '图 片 类 别';
          break;
        }
        case "type": {
          this.typeTitle = path[1];
          this.timeTitle = '拍 摄 时 间';
          this.selectAllImage('type', path[1]);
          break;
        }
        case "time": {
          this.timeTitle = path[1];
          this.typeTitle = '图 片 类 别';
          this.selectAllImage('time', path[1]);
          break;
        }
        case "search": {
          break;
        }
        default: break;
      }
    },
    selectAllImage(mode, value) {
      var _this = this;
      const formData = new FormData();
      let requestApi = '/image/selectAll';
      formData.append("currentPage", this.currentPage);
      formData.append("pageSize", this.pageSize);
      formData.append("token", this.token);
      if (mode == "type") {
        formData.append("imageType", value);
        requestApi = '/image/selectAllByType';
      }
      if (mode == "time") {
        formData.append("imageDate", value);
        requestApi = '/image/selectAllByTime';
      }
      this.axios({
        url: this.$serveUrL + requestApi,
        method: "post",
        data: formData
      }).then(function (resp) {
        if (resp.data.status == "success") {
          _this.images = resp.data.data.images;
          _this.previewImageUrL = resp.data.data.previewImageUrL;
          _this.totalCount = resp.data.data.totalCount;
          _this.isRouterAlive = !_this.isRouterAlive;

        }
      })
    },

    remoteMethod(query) {
      if (query !== '') {
        this.loading = true;
        setTimeout(() => {
          this.loading = false;
          this.options = this.list.filter(item => {
            return item.label.toLowerCase()
              .indexOf(query.toLowerCase()) > -1;
          });
        }, 200);
      } else {
        this.options = [];
      }
    },

    // 显示或隐藏顶部菜单和选中菜单
    SwitchDisplay(checkedCount) {
      if (checkedCount > 0) {
        // 获取顶部菜单高度，使选中菜单保持一致
        let h = this.$refs.top_menu.$el["offsetHeight"];
        if (this.displayTopMenu != "display:none;" || this.displayTopMenu == '') {
          this.displayCheckedMenu = 'display:block;height:' + h + 'px;';
          this.displayTopMenu = 'display:none;'
        }
      }
      else {
        this.displayCheckedMenu = '';
        this.displayTopMenu = '';
      }
    },
    //全选框
    handleCheckAllChange(val) {
      if (val) {
        let len = this.images.length;
        this.checkedCount = len;
        this.checkedImgIndex = new Array(len);
        for (var i = 0; i < len; i++) {
          this.checkedImgIndex[i] = i;
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
      this.checkedImgId = new Array(len);
      for (let i = 0; i < len; i++) {
        this.checkedImgId[i] = this.images[this.checkedImgIndex[i]].imageId;
      }
    },
    // 部分被选中,当勾选时，上方显示操作菜单
    handleCheckedImgChange(value) {
      this.checkedCount = value.length;
      this.SwitchDisplay(this.checkedCount);
      this.checkAll = this.checkedCount === this.images.length;
      this.isIndeterminate = this.checkedCount > 0 && this.checkedCount < this.images.length;
    },
    //分页条
    handleSizeChange(val) {
      //console.log(`每页 ${val} 条`);
      // 重新设置每页显示的条数
      let Wstr = ['10%', '10%', '9%', '9%', '9%', '9%', '9%'];
      let Hstr = ['29%', '29%', '29%', '29%', '26%', '27%', '21%'];
      this.reactiveImage.width = Wstr[val / 5 - 2];
      this.reactiveImage.height = Hstr[val / 5 - 2];

      this.pageSize = val;


      this.handleSelect('', this.arrPath);
    },
    handleCurrentChange(val) {
      //console.log(`当前页: ${val}`);
      // 重新设置当前页码
      this.currentPage = val;
      this.handleSelect('', this.arrPath);
    },

  }
}
</script>
<style scoped>
#main {
  width: 100%;
  height: 100%;
}

#check_menu {
  background-color: #3174ff;
  height: 8%;
  width: 100%;
  z-index: 10;
  display: none;
  position: relative;
}

.check_menu_txt {
  font-family: MiSans, MIUI, Helvetica Neue, Helvetica, Arial, PingFang SC, Microsoft Yahei, Hiragino Sans GB, Heiti SC, WenQuanYi Micro Hei, sans-serif;
  color: #fff;
  font-size: 1.35em;
}

.check_menu_item {
  margin-left: 2em;
  margin-top: 1em;
  display: inline-block;

}

.check_menu_feature {
  float: right;
  margin-top: 1em;
  margin-right: 2.8em;
}

.check_menu_feature a {
  margin-right: 2em;
  font-size: 1.35em;
}

#image-content {
  margin-left: 1.6em;
  margin-top: 2em;
  width: 100%;
  height: 82%;
  overflow: scroll;
  overflow-x: hidden;
}

.image-item {
  margin: 15px 15px;
  width: 12em;
  height: 12em;
  position: relative;
  display: inline-block;
}

.image-item:hover .check-box {
  display: block;
}

.image {
  position: absolute;
  width: 100%;
  height: 100%;
}

.check-box {
  width: 100%;
  margin: 5px;
  display: none;
  position: absolute;
}



#pagination {
  height: 6%;
  width: 100%;
  position: relative;
}

#pagination-son {
  position: absolute;
  left: 50%;
  top: 10%;
  transform: translate(-50%, -10%);
}

.image-wrapper {
  position: relative;
  width: 100%;
  height: 100%;
}

.image {
  width: 100%;
  height: 100%;
  display: block;
  object-fit: cover;
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
<style >
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

/* 相册菜单 */
.el-menu--popup {
  max-height: 32em;
  overflow: auto;
  overflow-x: hidden;
}
</style>

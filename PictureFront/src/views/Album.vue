<template>
    <div id="main" :style="mainStyle">
        <!-- 隐藏菜单 -->
        <div id="check_menu" :style="displayCheckedMenu">
            <div class="check_menu_item"> <a @click="handleCheckAllChange(false)">
                    <svg-icon icon-file-name="chacha" />
                </a></div>
            <div class="check_menu_item check_menu_txt">已选择{{ checkedCount }}张相册</div>
            <div class="check_menu_feature">
                <a title="全选图片" @click="handleCheckAllChange(!checkedStatus)">
                    <svg-icon icon-file-name="all">
                    </svg-icon>
                </a>
                <a title="删除相册" @click="deleteAlbums()">
                    <svg-icon icon-file-name="delete" />
                </a>
            </div>
        </div>
        <!-- 标题 -->
        <div id="title" :style="displayTopMenu">
            <h1>相册集</h1>
        </div>
        <div>

            <el-row class="album">
                <!--添加相册(个体)  -->
                <el-col class="album-el-col" :span="3" :offset="1">
                    <a @click="buildAblum()">
                        <el-card :body-style="{ padding: '10px', }" shadow="hover">
                            <div class="album-add-icon">
                                <svg-icon icon-file-name="addAlbum" style="font-size:3em;margin-top: 1em;" />
                            </div>
                            <div class="album-el-txt">
                                <div style="text-align:center;font-size:21px;height:42px;"><a>新建相册</a>
                                </div>
                            </div>
                        </el-card>
                    </a>
                </el-col>

                <!-- 相册内容(循环) -->
                <el-col class="album-el-col" v-for="(item, index) in albumData" :key="index" :span="3" >
                    <a>
                        <!-- 相册外边距 -->
                        <el-card :body-style="{ padding: '10px', }" shadow="hover" class="card">
                            <el-checkbox-group v-model="checkedImgIndex" @change="handleCheckedImgChange"
                                class="check-box" :style="displayCheckedMenu">
                                <el-checkbox :label="index">
                                </el-checkbox>
                            </el-checkbox-group>
<!--                            <img class="album-el-img" :src=" item.albumImg" @click="openAblum(index)"-->
<!--                                style="object-fit:cover">-->
                          <img
                              class="album-el-img"
                              :src="item.albumImg"
                              @click="openAblum(index)"
                              @error="handleImgError($event, index)"
                              style="object-fit:cover"
                          />
                            <div class="album-el-txt">
                                <span>{{ item.albumName }}</span>
                                <div>图片数量:<a> {{ item.imageNumber }}张</a>
                                </div>
                            </div>
                        </el-card>
                    </a>
                </el-col>

            </el-row>
        </div>
    </div>

</template>
<script>
export default {
    data() {
        return {
            mainStyle: {
                height: '500px',
            },
            albumName: '示例相册',
            ImageNumber: 100,
            resizeTimer: null,
            // 服务器url
            token: '',
            serveUrL: this.$serveUrL,
            // 相册json数据
            albumData: [
                {
                    albumName: '相册',
                    albumImg: 'https://myc-picture.oss-cn-beijing.aliyuncs.com/image/avatar/albumImage.png',
                    albumId: '',
                    imageNumber: 100,
                }
            ],

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
        };
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
        this.selectAlbums();
        let h = window.document.body.clientHeight;
        this.mainStyle.height = (h * 0.75) + 'px';
        window.onresize = () => {
            console.log()
            if (this.resizeTimer) {
                clearTimeout(this.resizeTimer)
            }
            this.resizeTimer = setTimeout(() => {
                let h = window.document.body.clientHeight;
                this.mainStyle.height = (h * 0.75) + 'px';
            }, 500)
        };
    },
    methods: {
      handleImgError(event, index) {
        // 设置默认图片地址
        this.albumData[index].albumImg = 'https://myc-picture.oss-cn-beijing.aliyuncs.com/image/avatar/albumImage.png';
      },
        //打开相册
        openAblum(id) {
            let name = this.albumData[id].albumName;
            this.currentAlbumId = this.albumData[id].albumId;

            this.$message({
                type: 'success',
                message: '打开成功 '
            });
            this.$router.push({ path: '/InsideAlbum', query: { albumId: this.currentAlbumId, albumName: name } });
        },
        // 查询所有相册
        selectAlbums() {
            var _this = this;
            const formData = new FormData();
            formData.append('token', this.token);
            this.axios({
                url: this.serveUrL + "/album/selectAlbumName",
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
        //新建相册
        buildAblum() {
            this.$prompt('请输入新的相册名', '提示', {
                confirmButtonText: '确定',
                cancelButtonText: '取消',
            }).then(({ value }) => {
                this.handleCheckIndexToId();
                var _this = this;
                const formData = new FormData();
                formData.append('token', this.token);
                formData.append('albumName', value);
                this.axios({
                    url: this.$serveUrL + "/album/addAlbum",
                    method: "post",
                    data: formData
                }).then(function (resp) {
                    if (resp.data.status == "success") {
                        _this.dialogTableVisible = false;
                        _this.$message({
                            type: 'success',
                            message: '你的相册名字是: ' + value
                        });
                        _this.selectAlbums();
                    }
                    else if (resp.data.status == "exist") {
                      _this.$message({
                        type: 'error',
                        message: '相册名重复'
                      });
                      _this.selectAlbums();
                    }
                })

            }).catch(() => {
                this.$message({
                    type: 'info',
                    message: '取消新建相册'
                });
            });
        },
        // 删除相册
        deleteAlbums() {
            this.$confirm('此操作将删除所选相册, 是否继续?', '删除相册集', {
                confirmButtonText: '确定',
                cancelButtonText: '取消',
                type: 'warning'
            }).then(() => {
                console.log(1)
                this.handleCheckIndexToId();
                var _this = this;
                const formData = new FormData();
                formData.append('token', this.token);
                formData.append('albumIds', this.checkedImgId);
                this.axios({
                    url: this.$serveUrL + "/album/deleteAlbumByIds",
                    method: "post",
                    data: formData
                }).then(function (resp) {
                    if (resp.data.status == "success") {
                        _this.$message({
                            message: '相册删除成功！',
                            type: 'success'
                        });
                        _this.handleCheckAllChange(false);
                        _this.selectAlbums();
                    }
                    else {
                        1;
                    }
                })
            })
        },
        SwitchDisplay(checkedCount) {
            if (checkedCount > 0) {
                // 获取顶部菜单高度，使选中菜单保持一致
                if (this.displayTopMenu != "display:none;" || this.displayTopMenu == '') {
                    this.displayCheckedMenu = 'display:block'
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
                let len = this.albumData.length;
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
                this.checkedImgId[i] = this.albumData[this.checkedImgIndex[i]].albumId;
            }
        },
        // 部分被选中,当勾选时，上方显示操作菜单
        handleCheckedImgChange(value) {
            console.log(value.length)
            this.checkedCount = value.length;
            this.SwitchDisplay(this.checkedCount);
            this.checkAll = this.checkedCount === this.albumData.length;
            this.isIndeterminate = this.checkedCount > 0 && this.checkedCount < this.albumData.length;
        },
    },
}
</script>
<style scoped>
#main {
    width: 100%;
    overflow-y: auto;
    overflow-x: hidden;
}


#check_menu {
    background-color: #3174ff;
    height: 55px;
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


.check-box {
    margin: 5px;
    display: none;
    position: absolute;
}

/* 整体相册 */


@media screen and (min-width: 960px) {

    #title {
        height: 55px;
        text-align: center;
        font-size: 2em;

        color: transparent;

    }

    .album {
        margin-left: 2em;
    }

    .album-el-col {
        margin: 0.9em;
        width: 12em;
    }

    .album-add-icon {
        text-align: center;
        height: 10em;
    }

    .album-el-img {
        /* 消去label */
        position: relative;
        width: 100%;
        height: 10em;
    }
}

@media screen and (max-width: 960px) {

    #title {
        height: 50px;
        text-align: center;
        font-size: 1.5em;
        color: transparent;

    }

    .album {
        margin-left: 1em;
    }

    .album-add-icon {
        text-align: center;
        height: 7em;
    }

    .album-el-col {
        margin: 0.5em;
        width: 10em;
    }

    .album-el-img {
        /* 消去label */
        position: relative;
        width: 100%;
        height: 7em;
    }
}



.album-el-txt {
    margin-top: 0.8em
}

.card:hover .check-box {
    display: block;
}
</style>
<style>
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
  
 
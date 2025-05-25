<template>
  <div class="loginMain">
    <div class="login">
      <img :src="logoImg"  class="logo-img">

      <a-form id="components-form-demo-normal-login" :form="form" class="login-form" @submit="handleSubmit">
        <a-form-item>
          <a-input
              v-decorator="[
              'userName',
              { rules: [{ required: true, message: '请输入你的用户名或邮箱' }] },
            ]"
              placeholder="用户名/邮箱"
              size="large"
              :allowClear="true"
          >
            <a-icon slot="prefix" type="user" style="color: rgba(0,0,0,.25)" />
          </a-input>
        </a-form-item>

        <a-form-item>
          <a-input
              v-decorator="[
              'passWord',
              { rules: [{ required: true, message: '请输入你的密码' }] },
            ]"
              type="password"
              placeholder="密码"
              size="large"
              :allowClear="true"
          >
            <a-icon slot="prefix" type="lock" style="color: rgba(0,0,0,.25)" />
          </a-input>
        </a-form-item>

        <a-form-item>
          <div class="form-options">
            <a-checkbox
                v-decorator="[
                'remember',
                {
                  valuePropName: 'checked',
                  initialValue: true,
                },
              ]"
            >
              记住密码
            </a-checkbox>
            <a class="login-form-link" @click="gotoForgot">忘记密码？</a>
          </div>

          <a-button type="primary" html-type="submit" class="login-form-button" size="large">
            登录
          </a-button>
        </a-form-item>
        <!-- 把注册部分单独放到这里，登录按钮下方 -->
        <div class="form-register">
          <span>没有账号？</span>
          <a @click="gotoRegister">现在注册</a>
        </div>
      </a-form>
    </div>
  </div>
</template>

<script>
export default {
  name: 'login',
  data() {
    return {
      logoImg: require('../assets/logo/logo2.png'),
    };
  },
  beforeCreate() {
    this.form = this.$form.createForm(this, { name: 'normal_login' });
  },
  methods: {
    gotoRegister() {
      this.$router.push('/register');
    },
    gotoForgot() {
      this.$router.push('/forgot-password');
    },
    handleSubmit(e) {
      e.preventDefault();
      this.form.validateFields((err, values) => {
        var _this = this;
        if (err) {
          this.$message({
            message: '请填写完整登录信息!',
            type: 'warning',
          });
        } else {
          this.axios({
            url: _this.$serveUrL + '/user/login',
            method: 'post',
            data: values,
            transformRequest: [
              function (data) {
                let ret = '';
                for (let it in data) {
                  ret += encodeURIComponent(it) + '=' + encodeURIComponent(data[it]) + '&';
                }
                ret = ret.substring(0, ret.lastIndexOf('&'));
                return ret;
              },
            ],
            headers: {
              'Content-Type': 'application/x-www-form-urlencoded',
            },
          }).then(function (resp) {
            var data = resp.data;
            if (data.code == '200') {
              _this.$cookie.setCookie('token', data.data, 0.5); // 保存 token
              _this.$message({
                message: '恭喜你，登陆成功!',
                type: 'success',
              });
              _this.$router.push({ name: 'Home', query: { status: true } });
            } else {
              _this.$message({
                message: '账号或密码不正确,登陆失败!',
                type: 'error',
              });
            }
          });
        }
      });
    },
  },
};
</script>

<style scoped>
.loginMain {
  width: 100%;
  height: 100%;
  background-image: url('../assets/logo/back.png');
}

.login-form-button {
  width: 100%;
  margin-top: 20px;
}

.login-form-link {
  font-size: 14px;
  color: #1890ff;
  cursor: pointer;
}

.login-form-text {
  font-size: 14px;
  color: #666;
}

.form-options {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

@media screen and (max-width: 512px) {
  .login {
    width: 320px;
    height: 200px;
    position: absolute;
    top: 50%;
    left: 50%;
    margin-top: -250px;
    margin-left: -160px;
  }

  .login img {
    height: 120px;
    margin-bottom: 20px;
  }

  #components-form-demo-normal-login .a-input {
    height: 80px;
  }
}

@media screen and (min-width: 512px) {
  .login {
    width: 420px;
    height: 200px;
    position: absolute;
    top: 50%;
    left: 50%;
    margin-top: -250px;
    margin-left: -210px;
  }

  .login img {
    height: 150px;
  }

  #components-form-demo-normal-login .login-form {
    max-width: 300px;
  }

  #components-form-demo-normal-login .a-input {
    height: 150px;
  }
  .form-register {
    display: flex;
    justify-content: flex-end;
    align-items: center;
    margin-top: 10px;
    gap: 4px;
    font-size: 14px;
  }
  .form-register a {
    color: #1890ff;
    cursor: pointer;
  }
  .logo-img {
    display: block;
    margin-left: 90px; /* 向右移动 */
    margin-top: -30px; /* 向上移动 */
  }
}
</style>
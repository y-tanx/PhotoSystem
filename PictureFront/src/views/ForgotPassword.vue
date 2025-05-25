<!--修改了: index.js, login.vue, ForgotPassword.vue -->
<template>
  <div class="registerMain">
    <div class="register">
      <div class="title">
        <h1>找回密码</h1>
      </div>
      <a-form :form="form" @submit="handleSubmit">
        <!-- 用户名 -->
        <a-form-item v-bind="formItemLayout" label="账号">
          <a-input
              v-decorator="[
              'username',
              { required: true, message: '请输入您的用户名' }
            ]"
              size="large"
          />
        </a-form-item>

        <!-- 邮箱 -->
        <a-form-item v-bind="formItemLayout" label="E-mail">
          <a-input
              v-decorator="[
              'email',
              {
                type: 'email',
                message: '邮箱不合法!',
              },
              {
                required: true,
                message: '请输入你的邮箱!',
              }
            ]"
              size="large"
          />
        </a-form-item>

        <!-- 验证码 -->
        <a-form-item v-bind="formItemLayout" label="验证码">
          <a-input
              v-decorator="[
              'codeNumber',
              { required: true, message: '请输入验证码!' }
            ]"
              size="large"
              style="width: 50%; float: left; margin-right: 5%;"
              :maxLength="10"
          />
          <a-button type="primary" @click="sendCode()" :disabled="codeState">
            {{ codetxt }}
          </a-button>
        </a-form-item>

        <!-- 新密码 -->
        <a-form-item v-bind="formItemLayout" label="新密码" has-feedback>
          <a-input
              v-decorator="[
              'password',
              {
                required: true,
                message: '请输入新密码!',
              },
              {
                validator: validateToNextPassword,
              },
            ]"
              type="password"
              size="large"
          />
        </a-form-item>

        <!-- 确认新密码 -->
        <a-form-item v-bind="formItemLayout" label="确认密码" has-feedback>
          <a-input
              v-decorator="[
              'confirm',
              {
                required: true,
                message: '请再次输入新密码!',
              },
              {
                validator: compareToFirstPassword,
              },
            ]"
              type="password"
              @blur="handleConfirmBlur"
              size="large"
          />
        </a-form-item>

        <!-- 操作按钮 -->
        <a-form-item v-bind="tailFormItemLayout">
          <a-button type="primary" html-type="submit">
            重置密码
          </a-button>
          <a-button @click="gotoLogin" style="margin-left:100px" type="primary">
            返回登录
          </a-button>
        </a-form-item>
      </a-form>
    </div>
  </div>
</template>

<script>
export default {
  data() {
    return {
      codeState: false,
      codetxt: '获取验证码',
      confirmDirty: false,
      formItemLayout: {
        labelCol: { xs: { span: 24 }, sm: { span: 8 } },
        wrapperCol: { xs: { span: 24 }, sm: { span: 16 } },
      },
      tailFormItemLayout: {
        wrapperCol: { xs: { span: 24 }, sm: { span: 16, offset: 8 } },
      },
    };
  },
  beforeCreate() {
    this.form = this.$form.createForm(this, { name: 'resetPassword' });
  },
  methods: {
    handleConfirmBlur(e) {
      const value = e.target.value;
      this.confirmDirty = this.confirmDirty || !!value;
    },
    compareToFirstPassword(rule, value, callback) {
      const form = this.form;
      if (value && value !== form.getFieldValue('password')) {
        callback('两次输入的密码不一致');
      } else {
        callback();
      }
    },
    validateToNextPassword(rule, value, callback) {
      const form = this.form;
      if (value && this.confirmDirty) {
        form.validateFields(['confirm'], { force: true });
      }
      callback();
    },
    sendCode() {
      let email = this.form.getFieldValue('email');
      let error = this.form.getFieldError('email');
      if (!email || error) {
        this.form.validateFields(['email']);
        return;
      }

      const t = 60;
      let i = t;
      this.codeState = true;
      let timer = setInterval(() => {
        this.codetxt = `${i}s后重新获取`;
        if (i === 0) {
          this.codeState = false;
          this.codetxt = '获取验证码';
          clearInterval(timer);
        }
        i--;
      }, 1000);

      let data = new FormData();
      data.append('email', email);
      this.axios({
        url: this.$serveUrL + "/code",
        method: "post",
        data: data,
        headers: {
          'Content-Type': 'application/x-www-form-urlencoded'
        }
      }).then(() => {
        this.$message.success("验证码已发送");
      });
    },
    handleSubmit(e) {
      e.preventDefault();
      this.form.validateFieldsAndScroll((err, values) => {
        if (err) {
          this.$message.warning('请填写完整信息!');
          return;
        }

        this.axios({
          url: this.$serveUrL + "/user/resetPassword", // 🔁 使用重置密码接口
          method: "post",
          data: values,
          transformRequest: [
            function (data) {
              let ret = ''
              for (let it in data) {
                ret += encodeURIComponent(it) + '=' + encodeURIComponent(data[it]) + '&'
              }
              return ret.slice(0, -1)
            }
          ],
          headers: {
            'Content-Type': 'application/x-www-form-urlencoded'
          }
        }).then(resp => {
          if (resp.data.status === "success") {
            this.$message.success("密码重置成功！");
            this.$router.push({ name: 'login' });
          } else if (resp.data.status === "codeError") {
            this.$message.error("验证码错误！");
          } else if (resp.data.status === "notExist") {
            this.$message.error("用户不存在！");
          } else {
            this.$message.error("密码重置失败！");
          }
        });
      });
    },
    gotoLogin() {
      this.$router.go(-1);
    }
  }
};
</script>

<style scoped>
.registerMain {
  width: 100%;
  height: 100%;
  background-image: url("../assets/logo/back.png");
}

@media screen and (max-width: 512px){
  .title { text-align: center; margin-left: 30px; margin-bottom: 30px; }
  .register {
    width: 320px;
    position: absolute;
    top: 38%;
    left: 50%;
    margin-top: -250px;
    margin-left: -160px;
  }
}

@media screen and (min-width: 512px){
  .title { text-align: center; margin-left: 130px; margin-bottom: 30px; }
  .register {
    width: 450px;
    position: absolute;
    top: 45%;
    left: 50%;
    margin-top: -250px;
    margin-left: -210px;
  }
}
</style>
import { ref } from 'vue';
import { defineStore } from 'pinia';
import { jwtDecode } from 'jwt-decode';
import { useRouter } from 'vue-router';
import {
  userConfirm,
  findById,
  regenerateToken,
  logout,
  registToken,
  changeImg,
  deleteImg,
  getImg,
  updateUser,
  deleteUser,
  forceLogout,
  registUser,
} from '@/api/user.js';
import { httpStatusCode } from '@/util/http-status.js';

export const useUserStore = defineStore(
  'user',
  () => {
    const router = useRouter();
    const isLogin = ref(false);
    const isLoginError = ref(false);
    const isValidToken = ref(false);
    const userInfo = ref({});

    const storeRegistUser = async (user) => {
      console.log('store = ', user);
      await registUser(
        user,
        (response) => {
          alert('회원가입 성공');
          router.push('/login');
        },
        (error) => {}
      );
    };

    const userLogin = async (loginUser) => {
      await userConfirm(
        loginUser,
        (response) => {
          if (response.status === httpStatusCode.OK) {
            let { data } = response;
            let accessToken = data['accessToken'];
            let refreshToken = data['refreshToken'];
            isLogin.value = true;
            isLoginError.value = false;
            isValidToken.value = true;
            sessionStorage.setItem('isLogin', isLogin.value);
            sessionStorage.setItem('accessToken', accessToken);
            sessionStorage.setItem('refreshToken', refreshToken);
            router.push('/');
          }
        },
        async (error) => {
          if (error.response.status == httpStatusCode.UNAUTHORIZED) {
            alert('아이디 혹은 비밀번호가 틀렸습니다. 확인 후 다시 시도해주세요.');
          }
        }
      );
    };

    const storeGetUserInfo = async (token) => {
      let decodeToken = jwtDecode(token);
      console.log('token id = ', decodeToken.userId);
      await findById(
        decodeToken.userId,
        (response) => {
          if (response.status == httpStatusCode.OK) {
            userInfo.value = {
              ...response.data.user,
            };
            sessionStorage.setItem('userInfo', JSON.stringify(userInfo.value));
            console.log('user = ', userInfo.value);
          }
        },
        async (error) => {
          console.log('error =,', error.message);
          if (error.response.status == httpStatusCode.UNAUTHORIZED) {
            await forceLogout(userInfo.value.userId, (response) => {
              isLogin.value = false;
              isLoginError.value = false;
              isValidToken.value = false;
              userInfo.value = {};
              sessionStorage.removeItem('isLogin');
              sessionStorage.removeItem('accessToken');
              sessionStorage.removeItem('refreshToken');
              sessionStorage.removeItem('userInfo');
              router.push('/login');
            });
          } else if (error.response.status == httpStatusCode.FORBIDDEN) {
            let refreshToken = sessionStorage.getItem('refreshToken');
            await regenerateToken(
              refreshToken,
              async (response3) => {
                const newAccessToken = response3.data.accessToken;
                sessionStorage.setItem('accessToken', newAccessToken);

                await storeGetUserInfo(newAccessToken);
              },
              (error) => {
                console.log(error);
              }
            );
          }
        }
      );
    };

    const storeRegenerateToken = async () => {
      await regenerateToken(
        JSON.stringify(userInfo.value),
        (response) => {
          let accessToken = response.data.accessToken;
          isValidToken.value = true;
          sessionStorage.setItem('accessToken', accessToken);
        },
        async (error) => {
          if (error.response.status == httpStatusCode.UNAUTHORIZED) {
            await forceLogout(userInfo.value.userId, (response) => {
              isLogin.value = false;
              isLoginError.value = false;
              isValidToken.value = false;
              userInfo.value = {};
              sessionStorage.removeItem('isLogin');
              sessionStorage.removeItem('accessToken');
              sessionStorage.removeItem('refreshToken');
              sessionStorage.removeItem('userInfo');
              router.push('/login');
            });
          }
        }
      );
    };

    const storeLogOut = async () => {
      await logout(
        userInfo.value.userId,
        (response) => {
          if (response.status == httpStatusCode.OK) {
            isLogin.value = false;
            isValidToken.value = false;
            isLoginError.value = false;
            userInfo.value = false;
            sessionStorage.removeItem('accessToken');
            sessionStorage.removeItem('refreshToken');
            sessionStorage.removeItem('isLogin');
            sessionStorage.removeItem('userInfo');
            alert('로그아웃에 성공했습니다.');
            router.push('/');
          }
        },
        async (error) => {
          if (error.response.status == httpStatusCode.UNAUTHORIZED) {
            await forceLogout(userInfo.value.userId, (response) => {
              isLogin.value = false;
              isLoginError.value = false;
              isValidToken.value = false;
              userInfo.value = {};
              sessionStorage.removeItem('isLogin');
              sessionStorage.removeItem('accessToken');
              sessionStorage.removeItem('refreshToken');
              sessionStorage.removeItem('userInfo');
              router.push('/login');
            });
          } else if (error.response.status == httpStatusCode.FORBIDDEN) {
            let refreshToken = sessionStorage.getItem('refreshToken');
            await regenerateToken(
              refreshToken,
              async (response3) => {
                const newAccessToken = response3.data.accessToken;
                sessionStorage.setItem('accessToken', newAccessToken);

                await storeLogOut();
              },
              (error) => {
                console.log(error);
              }
            );
          }
        }
      );
    };

    const storeRegistToken = async () => {
      let accessToken = sessionStorage.getItem('accessToken');
      let decodeAccessToken = jwtDecode(accessToken);
      await registToken(
        {
          userId: decodeAccessToken.userId,
          refreshToken: sessionStorage.getItem('refreshToken'),
        },
        (response) => {},
        async (error) => {
          if (error.response.status == httpStatusCode.UNAUTHORIZED) {
            await forceLogout(userInfo.value.userId, (response) => {
              isLogin.value = false;
              isLoginError.value = false;
              isValidToken.value = false;
              userInfo.value = {};
              sessionStorage.removeItem('isLogin');
              sessionStorage.removeItem('accessToken');
              sessionStorage.removeItem('refreshToken');
              sessionStorage.removeItem('userInfo');
              router.push('/login');
            });
          }
        }
      );
    };

    const storeChangeImage = async (formData) => {
      await changeImg(
        formData,
        (response) => {},
        async (error) => {
          alert('서버 저장 실패' + error.message);
          if (error.response.status == httpStatusCode.UNAUTHORIZED) {
            await forceLogout(userInfo.value.userId, (response) => {
              isLogin.value = false;
              isLoginError.value = false;
              isValidToken.value = false;
              userInfo.value = {};
              sessionStorage.removeItem('isLogin');
              sessionStorage.removeItem('accessToken');
              sessionStorage.removeItem('refreshToken');
              sessionStorage.removeItem('userInfo');
              router.push('/login');
            });
          } else if (error.response.status == httpStatusCode.FORBIDDEN) {
            let refreshToken = sessionStorage.getItem('refreshToken');
            await regenerateToken(
              refreshToken,
              async (response3) => {
                const newAccessToken = response3.data.accessToken;
                sessionStorage.setItem('accessToken', newAccessToken);

                await storeChangeImage(formData);
              },
              (error) => {
                console.log(error);
              }
            );
          }
        }
      );
    };

    const storeDeleteImage = async (userId) => {
      await deleteImg(
        userId,
        (response) => {
          userInfo.value.userImageDto = null;
        },
        async (error) => {
          if (error.response.status == httpStatusCode.UNAUTHORIZED) {
            await forceLogout(userInfo.value.userId, (response) => {
              isLogin.value = false;
              isLoginError.value = false;
              isValidToken.value = false;
              userInfo.value = {};
              sessionStorage.removeItem('isLogin');
              sessionStorage.removeItem('accessToken');
              sessionStorage.removeItem('refreshToken');
              sessionStorage.removeItem('userInfo');
              router.push('/login');
            });
          } else if (error.response.status == httpStatusCode.FORBIDDEN) {
            let refreshToken = sessionStorage.getItem('refreshToken');
            await regenerateToken(
              refreshToken,
              async (response3) => {
                const newAccessToken = response3.data.accessToken;
                sessionStorage.setItem('accessToken', newAccessToken);

                await storeDeleteImage(userId);
              },
              (error) => {
                console.log(error);
              }
            );
          }
        }
      );
    };

    const storeGetImage = async (userId) => {
      await getImg(
        userId,
        sessionStorage.getItem('accessToken'),
        (response) => {
          userInfo.value.imageSrc = response.data;
        },
        async (error) => {
          if (error.response.status == httpStatusCode.UNAUTHORIZED) {
            await forceLogout(userInfo.value.userId, (response2) => {
              isLogin.value = false;
              isLoginError.value = false;
              isValidToken.value = false;
              userInfo.value = {};
              sessionStorage.removeItem('isLogin');
              sessionStorage.removeItem('accessToken');
              sessionStorage.removeItem('refreshToken');
              sessionStorage.removeItem('userInfo');
              router.push('/login');
            });
          } else if (error.response.status == httpStatusCode.FORBIDDEN) {
            let refreshToken = sessionStorage.getItem('refreshToken');
            await regenerateToken(
              refreshToken,
              async (response3) => {
                const newAccessToken = response3.data.accessToken;
                sessionStorage.setItem('accessToken', newAccessToken);

                await storeGetImage(userId);
              },
              (error) => {
                console.log(error);
              }
            );
          }
        }
      );
    };

    const storeUpdateUser = async (userObject) => {
      await updateUser(
        userObject,
        async (response) => {
          alert('회원정보 수정 성공');
          let token = sessionStorage.getItem('accessToken');
          await storeGetUserInfo(token);
          router.push('/mypage');
        },
        async (error) => {
          if (error && error.response.status == httpStatusCode.UNAUTHORIZED) {
            await forceLogout(userInfo.value.userId, (response) => {
              isLogin.value = false;
              isLoginError.value = false;
              isValidToken.value = false;
              userInfo.value = {};
              sessionStorage.removeItem('isLogin');
              sessionStorage.removeItem('accessToken');
              sessionStorage.removeItem('refreshToken');
              sessionStorage.removeItem('userInfo');
              router.push('/login');
            });
          } else if (error && error.response.status == httpStatusCode.FORBIDDEN) {
            let refreshToken = sessionStorage.getItem('refreshToken');
            await regenerateToken(
              refreshToken,
              async (response3) => {
                const newAccessToken = response3.data.accessToken;
                sessionStorage.setItem('accessToken', newAccessToken);

                await storeUpdateUser(user);
              },
              (error) => {
                console.log(error);
              }
            );
          }
        }
      );
    };
    const storeDeleteUser = async (user) => {
      await deleteUser(
        user,
        (response) => {
          if (response.status == httpStatusCode.OK) {
            isLogin.value = false;
            isValidToken.value = false;
            userInfo.value = {};
            isLoginError.value = false;
            sessionStorage.removeItem('accessToken');
            sessionStorage.removeItem('refreshToken');
            router.push('/');
          }
        },
        async (error) => {
          console.log('why error? error = ', error);
          if (error && error.response.status == httpStatusCode.UNAUTHORIZED) {
            await forceLogout(userInfo.value.userId, (response) => {
              isLogin.value = false;
              isLoginError.value = false;
              isValidToken.value = false;
              userInfo.value = {};
              sessionStorage.removeItem('isLogin');
              sessionStorage.removeItem('accessToken');
              sessionStorage.removeItem('refreshToken');
              sessionStorage.removeItem('userInfo');
              router.push('/login');
            });
          } else if (error.response.status == httpStatusCode.FORBIDDEN) {
            let refreshToken = sessionStorage.getItem('refreshToken');
            await regenerateToken(
              refreshToken,
              async (response3) => {
                const newAccessToken = response3.data.accessToken;
                sessionStorage.setItem('accessToken', newAccessToken);

                await storeDeleteUser(user);
              },
              (error) => {
                console.log(error);
              }
            );
          }
        }
      );
    };

    return {
      userLogin,
      isLoginError,
      isLogin,
      storeGetUserInfo,
      userInfo,
      storeRegenerateToken,
      storeLogOut,
      storeRegistToken,
      storeChangeImage,
      storeDeleteImage,
      storeGetImage,
      storeUpdateUser,
      storeDeleteUser,
      storeRegistUser,
    };
  },
  {
    persist: {
      path: ['isLogin', 'userInfo'],
    },
  }
);

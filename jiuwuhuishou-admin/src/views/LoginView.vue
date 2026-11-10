<script setup>
	import {
		nextTick,
		onBeforeUnmount,
		onMounted,
		reactive,
		ref
	} from 'vue'
	import {
		useRoute,
		useRouter
	} from 'vue-router'
	import {
		ElMessage
	} from 'element-plus'
	import {
		useAuthStore
	} from '@/stores/auth'
	import {
		BRAND_ADMIN_TITLE,
		BRAND_LOGO_TEXT
	} from '@/constants/brand'
	import {
		validateForm
	} from '@/utils/form'
	import {
		getCaptchaUrl,
		getUUID
	} from '@/utils/tool'

	const route = useRoute()
	const router = useRouter()
	const authStore = useAuthStore()
	const formRef = ref()
	const captchaImgRef = ref()
	const loading = ref(false)
	const captchaUrl = ref('')
	const captchaLoading = ref(true)
	const DEFAULT_VIEWPORT = 'width=1600, minimum-scale=0.1, maximum-scale=10, user-scalable=yes, viewport-fit=cover'
	const LOGIN_VIEWPORT = 'width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no, viewport-fit=cover'
	const form = reactive({
		username: '',
		password: '',
		captcha: '',
		uuid: ''
	})
	const rules = {
		username: [{
			required: true,
			message: '请输入账号',
			trigger: 'blur'
		}],
		password: [{
			required: true,
			message: '请输入密码',
			trigger: 'blur'
		}],
		captcha: [{
			required: true,
			message: '请输入验证码',
			trigger: 'blur'
		}],
	}

	function getRedirectTarget() {
		const redirect = Array.isArray(route.query.redirect) ? route.query.redirect[0] : route.query.redirect
		if (!redirect || typeof redirect !== 'string' || redirect.startsWith('/login')) {
			return '/dashboard'
		}
		return redirect
	}

	function refreshCaptcha() {
		captchaLoading.value = true
		captchaUrl.value = ''
		form.uuid = getUUID()
		const nextUrl = getCaptchaUrl(form.uuid)
		// 先进入加载态，下一帧再挂图片地址，避免旧图/半截图闪一下
		nextTick(() => {
			captchaUrl.value = nextUrl
		})
	}

	function onCaptchaLoad() {
		captchaLoading.value = false
	}

	function onCaptchaError() {
		captchaLoading.value = false
		captchaUrl.value = ''
	}

	function setViewport(content) {
		if (typeof document === 'undefined') return
		const viewport = document.querySelector('meta[name="viewport"]')
		viewport?.setAttribute('content', content)
	}

	async function blurActiveField() {
		if (typeof document === 'undefined') return
		const activeElement = document.activeElement
		if (activeElement instanceof HTMLElement) {
			activeElement.blur()
		}
		await nextTick()
	}

	async function handleLogin() {
		if (loading.value) {
			return
		}
		const valid = await validateForm(formRef)
		if (!valid) {
			return
		}
		loading.value = true
		try {
			await authStore.login(form)
			await blurActiveField()
			ElMessage.success('登录成功')
			window.location.replace(router.resolve(getRedirectTarget()).href)
		} catch (error) {
			ElMessage.error(error.msg || error.message || '登录失败')
			refreshCaptcha()
			form.captcha = ''
		} finally {
			loading.value = false
		}
	}

	refreshCaptcha()

	onMounted(() => {
		setViewport(LOGIN_VIEWPORT)
	})

	onBeforeUnmount(() => {
		setViewport(DEFAULT_VIEWPORT)
	})
</script>

<template>
	<div class="login-shell">
		<div class="login-grid">
			<section class="login-brand">
				<div class="login-brandb">
					<div class="login-brand__logo">{{ BRAND_LOGO_TEXT }}</div>
					<h1>{{ BRAND_ADMIN_TITLE }}</h1>
				</div>
			</section>
			<el-card shadow="never" class="login-card">
				<template #header>
					<div class="login-card__header">
						<strong>账号登录</strong>
						<!-- <span>演示账号：admin / 123456，operator / 123456</span> -->
					</div>
				</template>
				<el-form ref="formRef" :model="form" :rules="rules" label-position="top" @submit.prevent>
					<el-form-item label="账号" prop="username">
						<el-input v-model="form.username" placeholder="请输入账号" clearable />
					</el-form-item>
					<el-form-item label="密码" prop="password">
						<el-input v-model="form.password" type="password" show-password placeholder="请输入密码" />
					</el-form-item>
					<el-form-item label="验证码" prop="captcha">
						<div class="login-captcha">
							<el-input v-model="form.captcha" placeholder="请输入验证码" clearable
								@keyup.enter="handleLogin" />
							<div class="login-captcha__box" :class="{ 'is-loading': captchaLoading }" @click="!captchaLoading && refreshCaptcha()">
								<img
									v-show="!captchaLoading && captchaUrl"
									ref="captchaImgRef"
									:src="captchaUrl || undefined"
									alt="验证码"
									class="login-captcha__image"
									width="124"
									height="40"
									decoding="async"
									@load="onCaptchaLoad"
									@error="onCaptchaError"
								/>
								<div v-if="captchaLoading" class="login-captcha__placeholder">
									<span class="login-captcha__spinner"></span>
									<span>加载中</span>
								</div>
							</div>
						</div>
					</el-form-item>
					<el-button native-type="button" type="primary" :loading="loading" class="login-card__submit"
						@click="handleLogin">进入系统</el-button>
				</el-form>
			</el-card>
		</div>
	</div>
</template>

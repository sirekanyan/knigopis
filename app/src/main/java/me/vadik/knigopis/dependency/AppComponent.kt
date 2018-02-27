package me.vadik.knigopis.dependency

import dagger.Component
import me.vadik.knigopis.BookActivity
import me.vadik.knigopis.MainActivity
import me.vadik.knigopis.user.UserActivity
import javax.inject.Singleton

@Singleton
@Component(modules = [AppModule::class])
interface AppComponent {
    fun inject(activity: MainActivity)
    fun inject(activity: BookActivity)
    fun inject(activity: UserActivity)
}

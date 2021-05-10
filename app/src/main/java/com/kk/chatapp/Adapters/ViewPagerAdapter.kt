import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter





//Bu adapter Fragment ve başlıkları arraylist içinde tutar çağırılan başlığa göre fragment'i sunar

internal class ViewPagerAdapter(fragmentManager: FragmentManager) :
    FragmentPagerAdapter(fragmentManager) {
    private val fragments: ArrayList<Fragment>
    private val titles: ArrayList<String>

    init {
        fragments = ArrayList<Fragment>()
        titles = ArrayList<String>()
    }

    override fun getCount(): Int {
        return fragments.size   //eklenilen fragment kadar döndürür
    }

    override fun getItem(position: Int): Fragment {
        return fragments[position]
    }

    fun addFragment(fragment: Fragment, title: String) {
        fragments.add(fragment)    //fragmenti ekler
        titles.add(title)   //başlık kısmına main içinde belirlediğimiz başlığı ekler
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return titles[position]

    }
}

import KotlinFunctionLibrary.println
import KotlinFunctionLibrary.printlnAndReturn
import java.io.File

fun main() {
    File("C:\\Users\\shmue\\public-android\\app\\src\\main\\java").walk().filter{!it.isDirectory}.toList().println { it.map{ it1->it1.nameWithoutExtension}.joinToString("|",prefix="(V|D|E)/(",postfix = ")"){it1-> "($it1)"} }
}
//(V|D|E)/((BaseShiurimPageActivity)|(DownloadsPageActivity)|(SampleDragSelectionActivity)|(TestAutoDataAdapter)|(FavoritesPageActivity)|(HistoryPageActivity)|(HomePageActivity)|(IndividualPlaylistPageActivity)|(IndividualSpeakerPageActivity)|(ListOfPlaylistsPageActivity)|(ListOfSpeakersPageActivity)|(MoreFromThisActivity)|(ParentCategoriesPageActivity)|(RecentlyAddedShiurimPageActivity)|(SubcategoriesPageActivity)|(TabsActivity)|(TestingActivity)|(TestingActivity2-halfWayThroughTabLayoutRefactor)|(TestingActivity2)|(CategoryAdapter)|(ChooserFastScrollerAdapter)|(PlaylistAdapter)|(ShiurAdapter)|(SpeakerAdapter)|(Application)|(FlickrApi)|(FlickrResponse)|(PhotoInterceptor)|(PhotoResponse)|(FlickrFetchr)|(GalleryItem)|(PhotoGalleryActivity)|(PhotoGalleryFragment)|(PhotoGalleryViewModel)|(QueryPreferences)|(ThumbnailDownloader)|(All)|(AndroidFunctionLibrary)|(ApiCalls)|(CallbackListener)|(Category)|(CONSTANTS)|(DragSelectableActivity)|(HoldsShiurCard)|(KotlinFunctionLibrary)|(OneOfMyClasses)|(Playlist)|(ShiurFilterOption)|(Shiur)|(ShiurCategoryPage)|(ShiurFullPage)|(ShiurQuickSeriesShiurPage)|(ShiurSearchResultsPage)|(ShiurSpeakerPage)|(Speaker)|(TorahAdapter)|(TorahFilter)|(TorahFilterable)|(ChooserFastScrollerDialog)|(PlaylistsSortOrFilterDialog)|(ShiurimSortOrFilterDialog)|(ShiurOptionsBottomSheetDialog)|(BrowseFragment)|(HistoryDownloadsFavoriteFragments)|(MoreFromThisFragment)|(NestedScrollableHost)|(PageViewModel)|(RandomTest1)|(TimeAdder)|(SectionsPagerAdapter)|(SentryLogger)|(TestViewModel))
//(V|D|E)/((BaseShiurimPageActivity)|(DownloadsPageActivity)|(SampleDragSelectionActivity)|(TestAutoDataAdapter)|(FavoritesPageActivity)|(HistoryPageActivity)|(HomePageActivity)|(IndividualPlaylistPageActivity)|(IndividualSpeakerPageActivity)|(ListOfPlaylistsPageActivity)|(ListOfSpeakersPageActivity)|(MoreFromThisActivity)|(ParentCategoriesPageActivity)|(RecentlyAddedShiurimPageActivity)|(SubcategoriesPageActivity)|(TabsActivity)|(TestingActivity)|(TestingActivity2-halfWayThroughTabLayoutRefactor)|(TestingActivity2)|(CategoryAdapter)|(ChooserFastScrollerAdapter)|(PlaylistAdapter)|(ShiurAdapter)|(SpeakerAdapter)|(Application)|(FlickrApi)|(FlickrResponse)|(PhotoInterceptor)|(PhotoResponse)|(FlickrFetchr)|(GalleryItem)|(PhotoGalleryActivity)|(PhotoGalleryFragment)|(PhotoGalleryViewModel)|(QueryPreferences)|(ThumbnailDownloader)|(All)|(AndroidFunctionLibrary)|(ApiCalls)|(CallbackListener)|(Category)|(CONSTANTS)|(DragSelectableActivity)|(HoldsShiurCard)|(KotlinFunctionLibrary)|(OneOfMyClasses)|(Playlist)|(ShiurFilterOption)|(Shiur)|(ShiurCategoryPage)|(ShiurFullPage)|(ShiurQuickSeriesShiurPage)|(ShiurSearchResultsPage)|(ShiurSpeakerPage)|(Speaker)|(TorahAdapter)|(TorahFilter)|(TorahFilterable)|(ChooserFastScrollerDialog)|(PlaylistsSortOrFilterDialog)|(ShiurimSortOrFilterDialog)|(ShiurOptionsBottomSheetDialog)|(BrowseFragment)|(HistoryDownloadsFavoriteFragments)|(MoreFromThisFragment)|(NestedScrollableHost)|(PageViewModel)|(RandomTest1)|(TimeAdder)|(SectionsPagerAdapter)|(SentryLogger)|(TestViewModel))
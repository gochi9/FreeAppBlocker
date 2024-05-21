//package gmail.developer_formal.freeappblocker.initializers;
//
//import android.content.Context;
//import androidx.annotation.NonNull;
//import androidx.startup.Initializer;
//import gmail.developer_formal.freeappblocker.BlockersManager;
//import org.jetbrains.annotations.NotNull;
//
//import java.util.Collections;
//import java.util.List;
//
//public class BlockersManagerInit implements Initializer<BlockersManager> {
//    @NonNull
//    @NotNull
//    @Override
//    public BlockersManager create(@NonNull @NotNull Context context) {
//        return BlockersManager.getInstance(context);
//    }
//
//    @NonNull
//    @NotNull
//    @Override
//    public List<Class<? extends Initializer<?>>> dependencies() {
//        return Collections.emptyList();
//    }
//}

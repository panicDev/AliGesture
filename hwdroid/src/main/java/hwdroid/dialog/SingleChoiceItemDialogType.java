package hwdroid.dialog;

import hwdroid.dialog.item.DropdownDialogSingleChoiceItem;
import hwdroid.widget.ItemAdapter;
import hwdroid.widget.item.Item;

import java.lang.ref.WeakReference;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.View;

public class SingleChoiceItemDialogType extends DialogType {

    public static final int HANDLER_UPDATE_ITEM = 0x201;

    private UpdateHandler mUpdateHandler;

    public SingleChoiceItemDialogType(Context context, DropDownDialog popupWindow,
                                      DialogInterface dialogInterface, ItemAdapter adapter,
                                      View menuView) {
        super(context, popupWindow, dialogInterface, adapter, menuView);

        mUpdateHandler = new UpdateHandler(this);
    }

    @Override
    public Item createItemView(String text, boolean selected) {
        DropdownDialogSingleChoiceItem item = new DropdownDialogSingleChoiceItem(text);
        item.setChecked(selected);
        item.setEnabled(true);
        return item;
    }

    @Override
    public void showChild() {
        for (int i = 0; i < mItemList.size(); i++) {
            Item item = mItemList.get(i);
            item.setEnabled(true);
            mAdapter.add(item);
        }
    }

    @Override
    public void updateChoicesItem(int pos) {
        Message msg = new Message();
        msg.what = HANDLER_UPDATE_ITEM;
        msg.arg1 = pos;
        mUpdateHandler.sendMessage(msg);
    }

    public void callBack(int index) {
        if (mOnClickListener != null) {
            mOnClickListener.onClick(mDialogInterface, index);
            //Clicking on an item in the list will not dismiss the dialog.
            //Clicking on a button will dismiss the dialog.
            //cancelDialog();
        }
    }

    private static class UpdateHandler extends Handler {
        private final WeakReference<SingleChoiceItemDialogType> mType;

        public UpdateHandler(SingleChoiceItemDialogType type) {
            this.mType = new WeakReference<SingleChoiceItemDialogType>(type);
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case HANDLER_UPDATE_ITEM:
                    int pos = msg.arg1;
                    if (pos < 0 && pos > mType.get().mItemList.size()) return;

                    Item item;
                    for (int i = 0; i < mType.get().mItemList.size(); i++) {
                        item = mType.get().mItemList.get(i);

                        if (i == pos) {
                            item.setChecked(true);
                        } else {
                            item.setChecked(false);
                        }

                    }

                    if (!mType.get().mHandler.hasMessages(HANDLER_UPDATE_ADAPTER)) {
                        mType.get().mHandler.sendEmptyMessageDelayed(HANDLER_UPDATE_ADAPTER, 10);
                    }
                    break;
            }
        }
    }

}

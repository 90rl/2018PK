package com.amazingteam.competenceproject.ui;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.amazingteam.competenceproject.R;
import com.amazingteam.competenceproject.model.Template;

import java.util.ArrayList;
import java.util.List;

public class TemplateAdapter extends RecyclerView.Adapter<TemplateAdapter.TemplateViewHolder> {
    private static final String TAG = TemplateAdapter.class.getSimpleName();
    private List<Template> templates = new ArrayList<>();
    private Context context;

    public TemplateAdapter(List<Template> templates, Context context) {

        this.templates = templates;
        this.context = context;

    }

    @Override
    public TemplateAdapter.TemplateViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_templates, parent, false);


        return new TemplateViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TemplateAdapter.TemplateViewHolder holder, int position) {
        Template template = templates.get(position);
        holder.imageView.setImageResource(template.getTemplateIconID());

    }

    @Override
    public int getItemCount() {
        if (templates != null && templates.size() > 0) {
            return templates.size();
        }
        return 0;
    }

    public class TemplateViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;

        public TemplateViewHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.template_shortcut_view);
        }
    }

    public void setTemplates(List<Template> templates) {
        this.templates = templates;
    }


    public static class RecyclerItemClickListener implements RecyclerView.OnItemTouchListener {
        private OnItemClickListener mListener;

        public interface OnItemClickListener {
            public void onItemClick(View view, int position);

            public void onShowPress(View view, int position);
        }

        GestureDetector mGestureDetector;

        public RecyclerItemClickListener(Context context, final RecyclerView recyclerView, OnItemClickListener listener) {
            mListener = listener;
            mGestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }

                @Override
                public void onShowPress(MotionEvent e) {
                    View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                    if (child != null && mListener != null) {
                        mListener.onShowPress(child, recyclerView.getChildAdapterPosition(child));
                    }
                }
            });
        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView view, MotionEvent e) {
            View childView = view.findChildViewUnder(e.getX(), e.getY());
            if (childView != null && mListener != null && mGestureDetector.onTouchEvent(e)) {
                mListener.onItemClick(childView, view.getChildAdapterPosition(childView));
                return true;
            }
            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView view, MotionEvent motionEvent) {
        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
        }
    }


}

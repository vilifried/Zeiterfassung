package at.example.zeiterfassung.adapter;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import at.example.zeiterfassung.R;
import at.example.zeiterfassung.db.TimeDataContract;

public class IssueAdapter extends RecyclerView.Adapter<IssueAdapter.IssueViewHolder> {

    private final Context _context;
    private Cursor _data;

    public IssueAdapter(@NonNull Context context, Cursor data) {
        _context = context;
        _data = data;
        setHasStableIds(true);
    }

    @NonNull
    @Override
    public IssueViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int type) {
        LayoutInflater inflater = LayoutInflater.from(_context);
        View view = inflater.inflate(R.layout.item_issue_data, viewGroup, false);
        return new IssueViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull IssueViewHolder issueViewHolder, int position) {
        // Keine daten vorhanden
        if (_data == null) {
            return;
        }

        // Keine Daten an der Position
        if (!_data.moveToPosition(position)) {
            return;
        }

        int columnIndex = _data.getColumnIndex(TimeDataContract.IssueData.Columns.NUMBER);
        issueViewHolder.Number.setText(String.valueOf(_data.getLong(columnIndex)));
        columnIndex = _data.getColumnIndex(TimeDataContract.IssueData.Columns.TITLE);
        issueViewHolder.Title.setText(_data.getString(columnIndex));
        columnIndex = _data.getColumnIndex(TimeDataContract.IssueData.Columns.PRIORITY);
        if (_data.isNull(columnIndex)) {
            issueViewHolder.Priority.setText("");
        } else {
            issueViewHolder.Priority.setText(_data.getString(columnIndex));
        }
        columnIndex = _data.getColumnIndex(TimeDataContract.IssueData.Columns.STATE);
        if (_data.isNull(columnIndex)) {
            issueViewHolder.State.setText("");
        } else {
            issueViewHolder.State.setText(_data.getString(columnIndex));
        }
    }

    @Override
    public int getItemCount() {
        if (_data == null) {
            return 0;
        }

        return _data.getCount();
    }

    @Override
    public long getItemId(int position) {
        if (_data == null) {
            return -1L;
        }

        if (_data.moveToPosition(position)) {
            return _data.getLong(_data.getColumnIndex(TimeDataContract.IssueData.Columns.NUMBER));
        }

        return -1L;
    }

    public void swapData(Cursor newData) {
        if (_data != null) {
            // Cursor schließen
            _data.close();
        }

        _data = newData;
        notifyDataSetChanged();
    }

    public class IssueViewHolder extends RecyclerView.ViewHolder {
        final TextView Number;
        final TextView Title;
        final TextView Priority;
        final TextView State;

        public IssueViewHolder(@NonNull View itemView) {
            super(itemView);

            // Init Layout
            Number = itemView.findViewById(R.id.NumberValue);
            Title = itemView.findViewById(R.id.TitleValue);
            Priority = itemView.findViewById(R.id.PriorityValue);
            State = itemView.findViewById(R.id.StateValue);
        }
    }
}

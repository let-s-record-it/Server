package com.sillim.recordit.task.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.sillim.recordit.calendar.domain.Calendar;
import com.sillim.recordit.calendar.fixture.CalendarFixture;
import com.sillim.recordit.member.domain.Member;
import com.sillim.recordit.member.fixture.MemberFixture;
import com.sillim.recordit.task.fixture.TaskFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class TaskTest {

	private Member member;
	private Calendar calendar;
	private TaskGroup taskGroup;

	@BeforeEach
	void init() {
		member = MemberFixture.DEFAULT.getMember();
		calendar = CalendarFixture.DEFAULT.getCalendar(member);
		taskGroup = new TaskGroup(false, null, null);
	}

	@Test
	@DisplayName("할 일을 생성할 수 있다.")
	void createTask() {
		Task expected = TaskFixture.DEFAULT.get(calendar, taskGroup);
		Task task =
				Task.builder()
						.title(expected.getTitle())
						.description(expected.getDescription())
						.date(expected.getDate())
						.colorHex(expected.getColorHex())
						.calendar(calendar)
						.taskGroup(taskGroup)
						.build();

		assertAll(
				() -> {
					assertThat(task.getTitle()).isEqualTo(expected.getTitle());
					assertThat(task.getDescription()).isEqualTo(expected.getDescription());
					assertThat(task.getDate()).isEqualTo(expected.getDate());
					assertThat(task.getColorHex()).isEqualTo(expected.getColorHex());
					assertThat(task.isAchieved()).isEqualTo(false);
					assertThat(task.getCalendar()).isEqualTo(expected.getCalendar());
					assertThat(task.getTaskGroup()).isEqualTo(expected.getTaskGroup());
				});
	}
}
